package com.sirolf2009.lsw2017.client.net

import com.google.gson.Gson
import com.kstruct.gethostname4j.Hostname
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.sirolf2009.lsw2017.common.Queues
import com.sirolf2009.lsw2017.common.model.Handshake
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.common.model.NotifyWait
import com.sirolf2009.lsw2017.common.model.PointRequest
import java.io.Closeable
import java.io.IOException
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static com.sirolf2009.lsw2017.common.Queues.*

class Connector implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Stage stage
	val Connection connection
	val Channel channel
	val String acceptedQueue
	val String deniedQueue
	val String battlegroundQueue

	new(Stage stage) {
		this.stage = stage
		val factory = new ConnectionFactory()
		factory.host = "localhost"
		factory.port = 5672
		connection = factory.newConnection()
		channel = connection.createChannel()
		Queues.declareQueues(channel)
		acceptedQueue = channel.queueDeclare().queue
		deniedQueue = channel.queueDeclare().queue
		battlegroundQueue = channel.queueDeclare().queue
		CONNECTED.send(new Handshake(Hostname.hostname, acceptedQueue, deniedQueue, battlegroundQueue))

		channel.basicConsume(acceptedQueue, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
				byte[] body) throws IOException {
				val notify = new Gson().fromJson(new String(body), NotifySuccesful)
				dialogTimer('''«notify.teamName» has received «notify.points» points''', Duration.seconds(2))
			}
		})
		channel.basicConsume(deniedQueue, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
				byte[] body) throws IOException {
				val notify = new Gson().fromJson(new String(body), NotifyWait)
				dialogButton('''«notify.teamName» must wait a little while longer''')
			}
		})
		channel.basicConsume(battlegroundQueue, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
				byte[] body) throws IOException {
				val notify = new Gson().fromJson(new String(body), NotifyBattleground)
				dialogButton('''«notify.teamName» must now go to the battleground «notify.battleground»''')
			}
		})
	}

	def dialogTimer(String text, Duration duration) {
		dialog(text) [
			val stage = value
			val timeline = new Timeline(new KeyFrame(duration, [stage.hide()]))
			timeline.play()
		]
	}

	def dialogButton(String text) {
		dialog(text) [
			val stage = value
			val it = key
			children += new Button("OK") => [
				onAction = [
					stage.hide()
				]
			]
		]
	}

	def dialog(String text, (Pair<VBox, Stage>)=>void controls) {
		Platform.runLater [
			val dialog = new Stage()
			dialog.initStyle(StageStyle.UNDECORATED)
			dialog.initModality(Modality.APPLICATION_MODAL)
			dialog.initOwner(stage)
			val container = new StackPane() => [
				alignment = Pos.CENTER
				children += new VBox() => [
					alignment = Pos.CENTER
					children += new Text(text)
					controls.apply(it -> dialog)
				]
			]
			val dialogScene = new Scene(container, 300, 200)
			dialog.scene = dialogScene
			dialog.show()
		]
	}

	def requestPoints(PointRequest request) {
		log.info('''requesting «request»''')
		POINT_REQUEST.send(request)
	}

	def send(String channelName, Object object) {
		channel.basicPublish("", channelName, null, new Gson().toJson(object).bytes)
	}

	def send(String channelName, String message) {
		channel.basicPublish("", channelName, null, message.bytes)
	}

	override close() throws IOException {
		channel.close()
		connection.close()
	}

}
