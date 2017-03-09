package com.sirolf2009.lsw2017.server.net

import com.couchbase.client.java.Bucket
import com.couchbase.client.java.CouchbaseCluster
import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonObject
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.TimeUnit
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import rx.subjects.PublishSubject
import com.sirolf2009.lsw2017.common.model.PointRequest

class Database implements Closeable {

	static val Logger log = LogManager.getLogger()

	val CouchbaseCluster cluster
	val Bucket bucket
	
	@Accessors val PublishSubject<Pair<PointRequest, JsonDocument>> pointsAwarded

	new() {
		cluster = CouchbaseCluster.create("localhost")
		bucket = cluster.openBucket("lsw-2017", "iwanttodie123")
		bucket.bucketManager().createN1qlPrimaryIndex(true, false);
		
		pointsAwarded = PublishSubject.create()
		
		log.info("Database connection initialized")
	}
	
	def awardPoints(PointRequest request) {
		bucket.async.get(request.teamName).map[
			content.put("points", content.getInt("points")+request.points)
			content.put("lastCheckedIn", request.currentTime)
			content.put("checkedInCount", content.getInt("checkedInCount")+1)
			return it
		].map[
			return bucket.replace(it, 1, TimeUnit.SECONDS)
		].toBlocking.subscribe([
			try {
				pointsAwarded.onNext(request -> it)
			} catch(Exception e) {
				log.error("Failed to publish points awarded", e)
			}
		], [
			log.error("Failed to award points", it)
		])
	}
	
	def getPoints(String teamName) {
		return teamName.team.points
	}
	
	def createNewTeam(String teamName) {
		val team = JsonObject.create().put("points", 0).put("lastCheckedIn", 0).put("checkedInCount", 0)
		bucket.upsert(JsonDocument.create(teamName, team))
	}
	
	def doesTeamExist(String teamName) {
		return bucket.exists(teamName)
	}
	
	def getTeam(String teamName) {
		return bucket.get(teamName);	
	}
	
	def getPoints(JsonDocument document) {
		return document.content.get("points") as Integer
	}
	
	override close() throws IOException {
		bucket.close()
		cluster.disconnect()
	}

}
