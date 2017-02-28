package com.sirolf2009.lsw2017.leaderboard

import com.couchbase.client.core.CouchbaseException
import com.couchbase.client.java.CouchbaseCluster
import com.couchbase.client.java.query.N1qlQuery
import com.sirolf2009.lsw2017.leaderboard.model.Team
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.stage.Stage
import rx.Observable
import xtendfx.FXApp
import rx.schedulers.Schedulers

@FXApp class Leaderboard {
	
	val testData = FXCollections.observableArrayList(new Team(42, "sirolf2009", "Krummi"), new Team(-100, "Ólavur Riddararós", "Týr"), new Team(1234, "Rotlaust Tre Fell", "Wardruna"))
	
	override start(Stage it) throws Exception {
		title = "LSW 2017 leaderboard"
		
		val cluster = CouchbaseCluster.create("localhost")
		val bucket = cluster.openBucket("lsw-2017", "iwanttodie123")
		
		new Thread[
			while(true) {
				try {
					bucket.async.query(N1qlQuery.simple("SELECT table.*, meta(table).id FROM `lsw-2017` table order by points desc limit 40")).flatMap[result|
						result.errors.flatMap[e|Observable.error(new CouchbaseException("N1QL Error/Warning: " + e))].switchIfEmpty(result.rows)
					].map[value].map[
						return new Team(getInt("points"), getString("id"), "TODO")
					].subscribeOn(Schedulers.computation).subscribe[
						val existing = testData.findFirst[data|data.name.equals(name)]
						if(existing == null) {
							testData.add(it)
						} else {
							existing.likes = likes
						}
					]
					Thread.sleep(1000)
				} catch(Exception e) {
					e.printStackTrace()
				}
			}
		].start()
		
		scene = new Scene(new GridPane() => [
			columnConstraints.add(new ColumnConstraints() => [
				percentWidth = 10
			])
			columnConstraints.add(new ColumnConstraints() => [
				percentWidth = 80
			])
			columnConstraints.add(new ColumnConstraints() => [
				percentWidth = 10
			])
			rowConstraints += new RowConstraints() => [
				percentHeight = 10
			]
			rowConstraints += new RowConstraints() => [
				percentHeight = 80
			]
			rowConstraints += new RowConstraints() => [
				percentHeight = 10
			]
			add(new TableView() => [
				val likesCol = new TableColumn<Team, Number>("Likes")
				likesCol.cellValueFactory = [value.likesProperty]
				val nameCol = new TableColumn<Team, String>("Naam")
				nameCol.cellValueFactory = [value.nameProperty]
				val subcampCol = new TableColumn<Team, String>("Subkamp")
				subcampCol.cellValueFactory = [value.subcampProperty]
				columns += likesCol
				columns += nameCol
				columns += subcampCol
				columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
				
				items = testData
			], 1, 1)
		], 800, 600) => [
			stylesheets += "flatter.css"
			
		]
		show()
	}
	
}