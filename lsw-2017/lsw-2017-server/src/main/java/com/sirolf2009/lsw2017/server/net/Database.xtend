package com.sirolf2009.lsw2017.server.net

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.datastax.driver.mapping.Mapper
import com.datastax.driver.mapping.MappingManager
import com.sirolf2009.lsw2017.common.model.DBQueue
import com.sirolf2009.lsw2017.common.model.DBTeam
import com.sirolf2009.lsw2017.common.model.PointRequest
import io.reactivex.subjects.PublishSubject
import java.io.Closeable
import java.io.IOException
import java.time.Duration
import java.util.Date
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Arrays

class Database implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Cluster cluster
	val Session session
	val Mapper<DBTeam> mapperTeam
	val Mapper<DBQueue> mapperQueue
	
	@Accessors val PublishSubject<Pair<PointRequest, DBTeam>> pointsAwarded
	@Accessors val PublishSubject<Pair<PointRequest, DBTeam>> pointsDenied

	new() {
		cluster = Cluster.builder.addContactPoints("localhost").withPort(32769).build()
		session = cluster.connect("lsw2017")
		 
		val manager = new MappingManager(session)
		mapperTeam = manager.mapper(DBTeam)
		mapperQueue = manager.mapper(DBQueue)
		
		pointsAwarded = PublishSubject.create()
		pointsDenied = PublishSubject.create()
		
		log.info("Database connection initialized")
	}
	
	def getQueues() {
		mapperQueue.map(session.execute("SELECT * FROM lsw2017.queue")).all()
	}
	
	def getQueuesForTeam(DBTeam team) {
		mapperQueue.map(session.execute('''SELECT * FROM lsw2017.queue WERE battleground in («Arrays.asList(if(team.battleground1) 0 else 1, if(team.battleground2) 0 else 2, if(team.battleground3) 0 else 3, if(team.battleground4) 0 else 4, if(team.battleground5) 0 else 5, if(team.battleground6) 0 else 6).filter[it != 0].map[it+""].reduce[a,b|a+","+b]»)''')).all()
	}
	
	def awardPoints(PointRequest request) {
		mapperTeam.get(request.teamName) => [
			if(request.currentTime-lastCheckedIn.time > Duration.ofMinutes(1).toMillis) {
				it.points += request.points
				it.lastCheckedIn = new Date(request.currentTime)
				it.timesCheckedIn++
				save()
				pointsAwarded.onNext(request -> it)
			} else {
				pointsDenied.onNext(request -> it)
			}
		]
	}
	
	def getPoints(String teamName) {
		return teamName.team.points
	}
	
	def createNewTeam(String teamName) {
		val team = new DBTeam => [
			it.teamName = teamName
			teamNumber = 0 //TODO
			lastCheckedIn = new Date(0)
			points = 0
			timesCheckedIn = 0
		]
		team.save()
	}
	
	def doesTeamExist(String teamName) {
		return teamName.team !== null
	}
	
	def getTeam(String teamName) {
		return mapperTeam.get(teamName)	
	}
	
	def save(DBTeam team) {
		mapperTeam.save(team)
	}
	
	def saveAsync(DBTeam team) {
		mapperTeam.saveAsync(team)
	}
	
	override close() throws IOException {
		cluster.close()
	}

}
