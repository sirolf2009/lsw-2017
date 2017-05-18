package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.common.model.DBTeam
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
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

//Battleground to be played = the battleground that you have not gone to, and has the smallest queue
//You will not join a battleground that you have previously played
//Overview of battlegrounds and who's playing/queueing
//Generate unique barcodes

//Leaderboard shows how long the game will still last for
//last 10 mins; the points will dissappear and the timer will go fullscreen
class Server implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Connector connector
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
			log.debug("Awarding " + teamName + " " + points + " points")
			database.awardPoints(it)
		]
		database.pointsAwarded.subscribeOn(Schedulers.io).subscribe [
			log.info("Awarded " + value.teamName + " " + key.points + " points from "+key.hostName)
			connector.send(acceptedQueues.get(key.hostName), new NotifySuccesful(key.teamName, key.points))
//			if(value.timesCheckedIn % 6 == 0) {
			if(value.timesCheckedIn % 1 == 0) {
				log.info(value.teamName + " is now allowed to go to the battleground")
				connector.send(battlegroundQueues.get(key.hostName), new NotifyBattleground(value.teamName))
			}
		]
		database.pointsDenied.subscribeOn(Schedulers.io).subscribe [
			log.info("Denied " + value.teamName + " " + key.points + " points from "+key.hostName)
			connector.send(deniedQueues.get(key.hostName), new NotifyWait(key.teamName))
		]
	}
	
	def moveTeamToBattleground(Pair<PointRequest, DBTeam> team) {
		val idle = database.getIdleBattlegroundsForTeam(team.value)
		if(idle.size > 0) {
			val battleground = idle.get(0)
			database.addTeamToBattleground(battleground, team.value)
			return battleground
		} else {
			val queues = database.getJoinableQueuesForTeam(team.value)
			val joinable = queues.groupBy[battleground].entrySet.stream.sorted[a,b| a.value.size.compareTo(b.value.size)].findFirst
			if(joinable.isPresent) {
				val battleground = joinable.get.value.get(0)
				database.addTeamToQueue(battleground, team.value)
				return battleground
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
