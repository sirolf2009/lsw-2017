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
			log.info("Awarded " + value.teamName + " " + key.points + " points")
			connector.send(acceptedQueues.get(key.hostName), new NotifySuccesful(key.teamName, key.points))
		]
		database.pointsDenied.subscribeOn(Schedulers.io).subscribe [
			log.info("Denied " + value.teamName + " " + key.points + " points")
			connector.send(acceptedQueues.get(key.hostName), new NotifySuccesful(key.teamName, key.points))
		]

//		connector.facade.subject.subscribeOn(Schedulers.computation).subscribe [
//			log.debug(it)
//		]
//		connector.facade.subject.subscribeOn(Schedulers.computation).subscribe [
//			log.debug(it)
//			if(!database.doesTeamExist(teamName)) {
//				log.debug("Creating new team: " + teamName)
//				database.createNewTeam(teamName)
//			}
//			log.debug("Awarding " + teamName + " " + points + " points")
//			database.awardPoints(it)
//		]
//
//		database.pointsAwarded.subscribeOn(Schedulers.computation).subscribe [
//			log.info("Awarded " + value.teamName + " " + key.points + " points")
//			val connection = connector.server.connections.findFirst [ connection |
//				connection.ID == key.clientID
//			]
//			connection.sendTCP(new NotifySuccesful(value.teamName, value.points))
//			if(value.points % 6 == 0) {
//				log.info(value.teamName + " is now allowed to go to the battleground")
////				connection.sendTCP(new NotifyBattleground(value.teamName))
//			}
//		]
//		database.pointsDenied.subscribeOn(Schedulers.computation).subscribe [
//			log.info("Denied " + value.teamName + " " + key.points + " points")
////			val connection = connector.server.connections.findFirst [ connection |
////				connection.ID == key.clientID
////			]
////			connection.sendTCP(new NotifyWait(value.teamName))
//		]
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
