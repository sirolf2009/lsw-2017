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

	def getIdleBattlegroundsForTeam(DBTeam team) {
		val battlegrounds = team.calculateUnplayedBattlegrounds.toList()
		val queues = allQueues
		return battlegrounds.filter [interestingBattleground|
			queues.filter[it.battleground.equals(interestingBattleground)].size == 0
		].toList
	}

	def getJoinableQueuesForTeam(DBTeam team) {
		val battlegrounds = team.calculateUnplayedBattlegrounds.toList()
		val queues = allQueues.filter[second_battler === null].toList
		return queues.filter[battlegrounds.contains(battleground)].toList()
	}

	def getSmallestQueueForTeam(DBTeam team) {
		val battlegrounds = team.calculateUnplayedBattlegrounds.toList()
		val queues = allQueues
		return queues.filter[battlegrounds.contains(battleground)].groupBy[battleground].entrySet.stream.sorted [a,b|
			a.value.size.compareTo(b.value.size)
		].findFirst.get().key
	}

	def addTeamToQueue(DBQueue queue, DBTeam team) {
		queue.second_battler = team.teamName
		queue.second_joined = new Date()
		mapperQueue.save(queue)
	}

	def getAllQueues() {
		mapperQueue.map(session.execute("SELECT * FROM lsw2017.queue")).all
	}

	def addTeamToBattleground(int battleground, DBTeam team) {
		mapperQueue.save(new DBQueue() => [ queue |
			queue.battleground = battleground
			queue.first_battler = team.teamName
			queue.first_joined = new Date()
		])
	}

	def finishBattle(int battleground, String team) {
		session.execute('''DELETE FROM lsw2017.queue where battleground=«battleground» and first_battler='«team»' ''')
		session.execute('''UPDATE lsw2017.teams SET battleground«battleground»=true WHERE teamname='«team»' ''')
	}

	def awardBattlegroundPoints(PointRequest request, int battleground, int points) {
		mapperTeam.get(request.teamName) => [
			it.points += points
			save()
			finishBattle(battleground, request.teamName)
			pointsAwarded.onNext(request -> it)
		]
	}

	def awardPoints(PointRequest request, int points) {
		mapperTeam.get(request.teamName) => [
			if (request.currentTime - lastCheckedIn.time > Duration.ofMinutes(0).toMillis) {
				it.points += points
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
			teamNumber = 0 // TODO
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
