package com.sirolf2009.lsw2017.client

import com.github.plushaze.traynotification.notification.Notifications
import com.github.plushaze.traynotification.notification.TrayNotification
import com.sirolf2009.lsw2017.client.net.Connector
import com.sirolf2009.lsw2017.common.model.PointRequest
import java.net.NetworkInterface
import java.util.Properties
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.logging.log4j.LogManager
import xtendfx.FXApp

@FXApp class Client {

	val log = LogManager.logger

	override start(Stage stage) throws Exception {
		stage.title = "LSW 2017"
		userAgentStylesheet = STYLESHEET_CASPIAN
		
		
		val config = new Properties()
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "digital:2181")
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.name)
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.name)
		
		println("sending")
		val producer = new KafkaProducer(config)
		producer.send(new ProducerRecord("connected", NetworkInterface.networkInterfaces.nextElement.displayName))
		producer.close()
		println("send")
		

		val connector = new Connector()

		stage.scene = new Scene(new StackPane => [
			stylesheets += "client.css"
			children += new StackPane => [
				alignment = Pos.TOP_RIGHT
				children += new Label("") => [
					val setConnection = [ Object listener |
						if (connector.connected.get) {
							graphic = new ImageView(new Image("green_dot.png"))
							text = "connected"
						} else {
							graphic = new ImageView(new Image("red_dot.png"))
							text = "disconnected"
						}
					]
					setConnection.apply(null)
					connector.connected.addListener [ listener |
						setConnection.apply(listener)
					]
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
					connector.proxy.requestPoints(
						new PointRequest(connector.client.ID, team.text, Integer.parseInt(points.text),
							System.currentTimeMillis))
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
			children += new StackPane => [
				alignment = Pos.TOP_LEFT
				children += new ToggleButton("") => [
					onAction = [ event |
						stage.fullScreen = !stage.isFullScreen
					]
					styleClass += "fullscreen-button"
					minHeight = 32
					minWidth = 32
				]
				pickOnBounds = false
			]
		], 800, 600)
		stage.show()
	}

}
