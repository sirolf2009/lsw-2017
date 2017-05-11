package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.server.net.Connector
import com.sirolf2009.lsw2017.server.net.Database
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.io.IOException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.HashMap
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.common.model.NotifyWait
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.PointRequest
import com.sirolf2009.lsw2017.common.model.DBTeam

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
			if(value.timesCheckedIn % 6 == 0) {
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
