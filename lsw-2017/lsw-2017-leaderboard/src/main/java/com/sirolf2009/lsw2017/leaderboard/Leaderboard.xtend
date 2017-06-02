package com.sirolf2009.lsw2017.leaderboard

import com.datastax.driver.core.Cluster
import com.sirolf2009.lsw2017.leaderboard.model.Team
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Calendar
import java.util.Date
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import xtendfx.FXApp

import static extension com.sirolf2009.lsw2017.leaderboard.Names.*
import javafx.scene.text.Font
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.layout.CornerRadii

@FXApp class Leaderboard {

	val ObservableList<Team> teams = FXCollections.observableArrayList()
	val endDate = {
		val cal = Calendar.instance
		cal.set(2017, Calendar.JUNE, 3, 17, 0, 0)
		cal.time
	}

	override start(Stage it) {
		try {
			title = "LSW 2017 leaderboard"

			val cluster = Cluster.builder.addContactPoint("localhost").withPort(32769).build
			val session = cluster.connect("lsw2017")

			new Thread [
				while (true) {
					try {
						session.execute("SELECT * FROM teams").all.map [
							new Team(getString("teamname").villainName, getString("teamname"), getInt("points"),
								getString("teamname").subkamp, get("lastcheckedin", Date).time)
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

			scene = new Scene(new StackPane() => [
				val table = new GridPane() => [
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

				]
				children += table
				children += new StackPane() => [ pane |
					background = new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY))
					pane.alignment = Pos.BOTTOM_RIGHT
					pane.children += new Label("time") => [
						padding = new Insets(8)
						val timeFormat = new SimpleDateFormat("HH:mm:ss")
						new Thread [
							while (true) {
								Platform.runLater [
									val timeLeft = endDate.time - System.currentTimeMillis
									text = timeFormat.format(new Date(timeLeft))
									if (table.visible && Duration.ofMillis(timeLeft).toMinutes <= 10) {
										minHeight = 8000
										maxHeight = 8000
										minWidth = 8000
										maxWidth = 8000
										pane.alignment = Pos.CENTER
										alignment = Pos.CENTER
										font = new Font(font.name, 100)
										table.visible = false
									}
								]
								Thread.sleep(Duration.ofSeconds(1).toMillis)
							}
						].start()
					]
				]
			], 800, 600) => [
				stylesheets += "flatter.css"

			]
			show()
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

}
