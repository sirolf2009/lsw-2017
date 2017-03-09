package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.server.net.Connector
import com.sirolf2009.lsw2017.server.net.Database
import java.io.Closeable
import java.io.IOException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import rx.schedulers.Schedulers
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifySuccesful

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
			if(!database.doesTeamExist(teamName)) {
				log.debug("Creating new team: " + teamName)
				database.createNewTeam(teamName)
			}
			log.debug("Awarding " + teamName + " " + points + " points")
			database.awardPoints(it)
			log.info("Awarded " + teamName + " " + points + " points")
		]

		database.pointsAwarded.subscribeOn(Schedulers.computation).subscribe [
			val connection = connector.server.connections.findFirst [ connection |
				connection.ID == key.clientID
			]
			connection.sendTCP(new NotifySuccesful(value.id, value.content.getInt("points")))
			if(value.content.getInt("checkedInCount") % 6 == 0) {
				log.info(value.id + " is now allowed to go to the battleground")
				connection.sendTCP(new NotifyBattleground(value.id))
			}
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
