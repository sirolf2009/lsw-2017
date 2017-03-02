package com.sirolf2009.lsw2017.server

import com.sirolf2009.lsw2017.server.net.Connector
import com.sirolf2009.lsw2017.server.net.Database
import java.io.Closeable
import java.io.IOException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import rx.schedulers.Schedulers

class Server implements Closeable {
	
	static val Logger log = LogManager.getLogger()
	
	val Connector connector
	val Database database
	
	new() {
		connector = new Connector()
		database = new Database()
		
		connector.facade.subject.subscribeOn(Schedulers.computation).subscribe[
			if(!database.doesTeamExist(teamName)) {
				database.createNewTeam(teamName)
			}
			database.awardPoints(teamName, points, currentTime)
			log.info("Awarded "+teamName+" "+points+" points")
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