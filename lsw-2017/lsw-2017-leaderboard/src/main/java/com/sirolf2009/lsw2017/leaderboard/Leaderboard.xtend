package com.sirolf2009.lsw2017.leaderboard

import com.datastax.driver.core.Cluster
import com.sirolf2009.lsw2017.leaderboard.model.Team
import java.util.Date
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.stage.Stage
import xtendfx.FXApp

import static extension com.sirolf2009.lsw2017.leaderboard.Names.*

@FXApp class Leaderboard {

	val ObservableList<Team> teams = FXCollections.observableArrayList()

	override start(Stage it) throws Exception {
		title = "LSW 2017 leaderboard"

		val cluster = Cluster.builder.addContactPoint("localhost").withPort(32769).build
		val session = cluster.connect("lsw2017")

		new Thread [
			while (true) {
				try {
					session.execute("SELECT * FROM teams").all.map [
						new Team(getString("teamname").villainName, getString("teamname"), getInt("points"), getString("teamname").subkamp, get("lastcheckedin", Date).time)
					].forEach [
						val existing = teams.findFirst[data|data.name.equals(name)]
						if (existing === null) {
							teams.add(it)
						} else {
							existing.likes = likes
						}
					]
					Thread.sleep(1000)
				} catch (Exception e) {
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
				val teamCol = new TableColumn<Team, String>("Team")
				teamCol.cellValueFactory = [value.teamProperty]
				val nameCol = new TableColumn<Team, String>("Naam")
				nameCol.cellValueFactory = [value.nameProperty]
				val likesCol = new TableColumn<Team, Number>("Vind ik leuks")
				likesCol.cellValueFactory = [value.likesProperty]
				val subcampCol = new TableColumn<Team, String>("Subkamp")
				subcampCol.cellValueFactory = [value.subcampProperty]
				columns += teamCol
				columns += nameCol
				columns += likesCol
				columns += subcampCol
				columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
				items = teams
			], 1, 1)
		], 800, 600) => [
			stylesheets += "flatter.css"

		]
		show()
	}

}
