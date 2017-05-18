package com.sirolf2009.lsw2017.client.net

import com.github.plushaze.traynotification.notification.Notifications
import com.github.plushaze.traynotification.notification.TrayNotification
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
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.common.model.PointRequest
import java.io.Closeable
import java.io.IOException
import javafx.application.Platform
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static com.sirolf2009.lsw2017.common.Queues.*
import com.sirolf2009.lsw2017.common.model.NotifyWait
import com.sirolf2009.lsw2017.common.model.NotifyBattleground

class Connector implements Closeable {

	static val Logger log = LogManager.getLogger()

	val Connection connection
	val Channel channel
	val String acceptedQueue
	val String deniedQueue
	val String battlegroundQueue

	new() {
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
					println(new String(body))
				val notify = new Gson().fromJson(new String(body), NotifySuccesful)
				Platform.runLater [
					val notification = new TrayNotification()
					notification.title = "Succes"
					notification.message = '''«notify.teamName» has received «notify.points» points'''
					notification.notification = Notifications.SUCCESS
					notification.showAndDismiss(Duration.seconds(1))
				]
			}
		})
		channel.basicConsume(deniedQueue, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
				byte[] body) throws IOException {
					println(new String(body))
				val notify = new Gson().fromJson(new String(body), NotifyWait)
				Platform.runLater [
					val notification = new TrayNotification()
					notification.title = "Error"
					notification.message = '''«notify.teamName» must wait a little while longer'''
					notification.notification = Notifications.ERROR
					notification.showAndDismiss(Duration.seconds(1))
				]
			}
		})
		channel.basicConsume(battlegroundQueue, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
				byte[] body) throws IOException {
				val notify = new Gson().fromJson(new String(body), NotifyBattleground)
				Platform.runLater [
					val notification = new TrayNotification()
					notification.title = "Battleground"
					notification.message = '''«notify.teamName» must now go to the battleground «notify.battleground»'''
					notification.notification = Notifications.INFORMATION
					notification.showAndDismiss(Duration.seconds(1))
				]
			}
		})
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
