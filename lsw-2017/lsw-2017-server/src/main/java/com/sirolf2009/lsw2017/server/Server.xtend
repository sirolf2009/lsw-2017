package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.server.net.Connector
import com.sirolf2009.lsw2017.server.net.Database
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.io.IOException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.sirolf2009.lsw2017.common.model.NotifyWait

class Server implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Connector connector
	val Database database

	new() {
		connector = new Connector()
		database = new Database()

		connector.facade.subject.subscribeOn(Schedulers.computation).subscribe [
			log.debug(it)
		]
		connector.facade.subject.subscribeOn(Schedulers.computation).subscribe [
			log.debug(it)
			if(!database.doesTeamExist(teamName)) {
				log.debug("Creating new team: " + teamName)
				database.createNewTeam(teamName)
			}
			log.debug("Awarding " + teamName + " " + points + " points")
			database.awardPoints(it)
		]

		database.pointsAwarded.subscribeOn(Schedulers.computation).subscribe [
			log.info("Awarded " + value.teamName + " " + key.points + " points")
			val connection = connector.server.connections.findFirst [ connection |
				connection.ID == key.clientID
			]
			connection.sendTCP(new NotifySuccesful(value.teamName, value.points))
			if(value.points % 6 == 0) {
				log.info(value.teamName + " is now allowed to go to the battleground")
				connection.sendTCP(new NotifyBattleground(value.teamName))
			}
		]
		database.pointsDenied.subscribeOn(Schedulers.computation).subscribe [
			log.info("Denied " + value.teamName + " " + key.points + " points")
			val connection = connector.server.connections.findFirst [ connection |
				connection.ID == key.clientID
			]
			connection.sendTCP(new NotifyWait(value.teamName))
		]
	}

	override close() throws IOException {
		database.close()
	}

	def static void main(String[] ars) {
		var Server server
		try {
			server = new Server()
		} catch(Exception e) {
			log.fatal("Server exited with an error", e)
			server.close()
		}
	}

}
