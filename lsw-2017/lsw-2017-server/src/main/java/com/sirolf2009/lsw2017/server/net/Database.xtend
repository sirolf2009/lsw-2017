package com.sirolf2009.lsw2017.server.net

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.datastax.driver.mapping.Mapper
import com.datastax.driver.mapping.MappingManager
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

class Database implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Cluster cluster
	val Session session
	val Mapper<DBTeam> mapper
	
	@Accessors val PublishSubject<Pair<PointRequest, DBTeam>> pointsAwarded
	@Accessors val PublishSubject<Pair<PointRequest, DBTeam>> pointsDenied

	new() {
		cluster = Cluster.builder.addContactPoints("localhost").withPort(32769).build()
		session = cluster.connect("lsw2017")
		 
		val manager = new MappingManager(session)
		mapper = manager.mapper(DBTeam)
		
		pointsAwarded = PublishSubject.create()
		pointsDenied = PublishSubject.create()
		
		log.info("Database connection initialized")
	}
	
	def awardPoints(PointRequest request) {
		mapper.get(request.teamName) => [
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
		return mapper.get(teamName)	
	}
	
	def save(DBTeam team) {
		mapper.save(team)
	}
	
	def saveAsync(DBTeam team) {
		mapper.saveAsync(team)
	}
	
	override close() throws IOException {
		cluster.close()
	}

}
