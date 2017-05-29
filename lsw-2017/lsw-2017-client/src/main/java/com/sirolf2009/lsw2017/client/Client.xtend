package com.sirolf2009.lsw2017.client

import com.github.plushaze.traynotification.notification.Notifications
import com.github.plushaze.traynotification.notification.TrayNotification
import com.kstruct.gethostname4j.Hostname
import com.sirolf2009.lsw2017.client.net.Connector
import com.sirolf2009.lsw2017.common.model.PointRequest
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import xtendfx.FXApp

@FXApp class Client {

	val log = LogManager.logger

	override start(Stage stage) throws Exception {
		stage.title = "LSW 2017"
		userAgentStylesheet = STYLESHEET_CASPIAN

		val connector = new Connector(stage)

		stage.scene = new Scene(new StackPane => [
			stylesheets += "client.css"
			children += new StackPane => [
				alignment = Pos.TOP_CENTER
				children += new Label("SuperShurkenBoek") => [
					styleClass += "title"
				]
			]

			val team = new TextField() => [
				maxWidth = 400
				requestFocus
			]
			val points = new TextField() => [
				maxWidth = 400
			]

			val sendPointsToServer = [
				try {
					connector.requestPoints(new PointRequest(Hostname.hostname, team.text, points.text, System.currentTimeMillis))
					team.clear()
					points.clear()
					team.requestFocus()
				} catch (Exception e) {
					Platform.runLater [
						val notification = new TrayNotification()
						notification.title = "Failure!"
						notification.message = "Uh oh, something went wrong"
						notification.notification = Notifications.ERROR
						notification.showAndDismiss(Duration.seconds(1))
					]
					log.error("Failed to send point request to the server. Team={} Points={} Time={}", team.text,
						points.text, System.currentTimeMillis, e)
				}
			]

			team.addEventFilter(KeyEvent.KEY_PRESSED, [
				if (code == KeyCode.ENTER) {
					points.requestFocus()
				}
			])
			points.addEventFilter(KeyEvent.KEY_PRESSED, [
				if (code == KeyCode.ENTER) {
					sendPointsToServer.apply(it)
				}
			])

			children += new VBox => [
				alignment = Pos.CENTER
				children += new HBox => [
					alignment = Pos.CENTER
					children += new Text("Team")
					children += team
				]
				children += new HBox => [
					alignment = Pos.CENTER
					children += new Text("Points")
					children += points
				]
				children += new Button => [
					text = "Submit"
					onAction = [
						sendPointsToServer.apply(it)
					]
				]
			]
//			children += new StackPane => [
//				alignment = Pos.TOP_LEFT
//				children += new ToggleButton("") => [
//					onAction = [ event |
//						stage.fullScreen = !stage.isFullScreen
//					]
//					styleClass += "fullscreen-button"
//					minHeight = 32
//					minWidth = 32
//				]
//				pickOnBounds = false
//			]
		], 800, 600)
		stage.show()
	}

}
