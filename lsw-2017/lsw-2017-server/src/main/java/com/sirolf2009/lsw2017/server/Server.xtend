package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.common.model.DBTeam
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifyWait
import com.sirolf2009.lsw2017.common.model.PointRequest
import com.sirolf2009.lsw2017.server.net.Connector
import com.sirolf2009.lsw2017.server.net.Database
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.io.IOException
import java.util.HashMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.regex.Pattern
import static extension com.sirolf2009.lsw2017.server.PointsParser.*
import com.sirolf2009.lsw2017.common.model.NotifySuccesful

//SSH Server IP = 217.63.34.101:54132 of 54123
//5672 <- port rabbitmq
//Welkom6211
//vhost /floris
//rabbit 15672
//DONE Battleground to be played = the battleground that you have not gone to, and has the smallest queue
//DONE You will not join a battleground that you have previously played
//DONE Overview of battlegrounds and who's playing/queueing
//DONE Generate unique barcodes
//Leaderboard shows how long the game will still last for
//last 10 mins; the points will dissappear and the timer will go fullscreen
class Server implements Closeable {

	static val Logger log = LogManager.getLogger()
	static val pointsPattern = Pattern.compile("p([0-9]+)")
	static val battlegroundPattern = Pattern.compile("s([0-9])-([0-9]+)")

	extension val Connector connector
	val Database database
	val acceptedQueues = new HashMap<String, String>()
	val deniedQueues = new HashMap<String, String>()
	val battlegroundQueues = new HashMap<String, String>()

	new() {
		connector = new Connector()
		database = new Database()

		connector.connected.subscribe[log.info(it + " connected")]
		connector.connected.subscribe[acceptedQueues.put(name, acceptedQueue)]
		connector.connected.subscribe[deniedQueues.put(name, deniedQueue)]
		connector.connected.subscribe[battlegroundQueues.put(name, battlegroundQueue)]

		connector.pointRequest.subscribe[log.debug("Received " + it)]
		connector.pointRequest.subscribeOn(Schedulers.computation).subscribe [
			if (!database.doesTeamExist(teamName)) {
				log.info("Creating new team")
				database.createNewTeam(teamName)
			}
			if (points.matches(pointsPattern)) {
				val likes = points.extractNumber(pointsPattern)
				log.debug("Awarding " + teamName + " " + likes + " points")
				database.awardPoints(it, likes)
			} else if (points.matches(battlegroundPattern)) {
				val numbers = points.extractAllNumbers(battlegroundPattern)
				println(numbers)
				val battleground = numbers.get(0)
				val points = numbers.get(1)
				log.debug("Awarding " + teamName + " " + points + " points from battleground " + battleground)
				database.awardBattlegroundPoints(it, battleground, points)
			} else {
				log.warn("I don't know what this is supposed to mean " + it)
			}
		]
		database.pointsAwarded.subscribeOn(Schedulers.io).subscribe [
			log.info("Awarded " + value.teamName + " " + key.points + " points from " + key.hostName)
//			if(value.timesCheckedIn % 6 == 0) {
			if (value.timesCheckedIn % 2 == 0) {
				log.info(value.teamName + " is now allowed to go to the battleground")
				battlegroundQueues.get(key.hostName).send(
					new NotifyBattleground(value.teamName, moveTeamToBattleground(it)))
			} else {
				acceptedQueues.get(key.hostName).send(new NotifySuccesful(value.teamName))
			}
		]
		database.pointsDenied.subscribeOn(Schedulers.io).subscribe [
			log.info("Denied " + value.teamName + " " + key.points + " points from " + key.hostName)
			connector.send(deniedQueues.get(key.hostName), new NotifyWait(key.teamName))
		]
		database.battlegroundAwarded.subscribeOn(Schedulers.io).subscribe [
			log.info("Awarded " + value.teamName + " " + key.points + " points from " + key.hostName)
			acceptedQueues.get(key.hostName).send(new NotifySuccesful(value.teamName))
		]
	}

	def int moveTeamToBattleground(Pair<PointRequest, DBTeam> team) {
		val queues = database.getJoinableQueuesForTeam(team.value)
		val joinableEmpty = queues.groupBy[battleground].entrySet.stream.filter[value.size == 1].findFirst
		if (joinableEmpty.present) {
			val battleground = joinableEmpty.get().value.get(0)
			database.addTeamToQueue(battleground, team.value)
			return battleground.battleground
		}
		val idle = database.getIdleBattlegroundsForTeam(team.value)
		if (idle.size > 0) {
			val battleground = idle.get(0)
			database.addTeamToBattleground(battleground, team.value)
			return battleground
		} else {
			val joinable = queues.groupBy[battleground].entrySet.stream.sorted [ a, b |
				a.value.size.compareTo(b.value.size)
			].findFirst
			if (joinable.isPresent) {
				val battleground = joinable.get.value.get(0)
				database.addTeamToQueue(battleground, team.value)
				return battleground.battleground
			} else {
				val battleground = database.getSmallestQueueForTeam(team.value)
				database.addTeamToBattleground(battleground, team.value)
				return battleground
			}
		}
	}

	override close() throws IOException {
		database.close()
	}

	def static void main(String[] ars) {
		var Server server
		try {
			server = new Server()
		} catch (Exception e) {
			log.fatal("Server exited with an error", e)
			server.close()
		}
	}

}
