package com.sirolf2009.lsw2017.server.net

import com.google.gson.Gson
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.sirolf2009.lsw2017.common.Queues
import com.sirolf2009.lsw2017.common.model.PointRequest
import java.io.IOException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors

import static com.sirolf2009.lsw2017.common.Queues.*
import io.reactivex.subjects.Subject
import io.reactivex.subjects.ReplaySubject
import com.sirolf2009.lsw2017.common.model.Handshake
import com.rabbitmq.client.Channel

@Accessors class Connector {

	static val Logger log = LogManager.getLogger()
	
	val Channel channel
	val Subject<Handshake> connected
	val Subject<PointRequest> pointRequest

	new() {
		val factory = new ConnectionFactory()
		factory.host = "localhost"
		factory.port = 5672
		val connection = factory.newConnection()
		channel = connection.createChannel()
		Queues.declareQueues(channel)
		connected = ReplaySubject.create()
		channel.basicConsume(CONNECTED, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
				connected.onNext(new Gson().fromJson(new String(body), Handshake))
			}
		})
		pointRequest = ReplaySubject.create()
		channel.basicConsume(POINT_REQUEST, true, new DefaultConsumer(channel) {
			override handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
				pointRequest.onNext(new Gson().fromJson(new String(body), PointRequest))
			}
		})
		log.info("Connector initialized")
	}
	
	def send(String channelName, Object object) {
		channel.basicPublish("", channelName, null, new Gson().toJson(object).bytes)
	}
	
	def send(String channelName, String message) {
		channel.basicPublish("", channelName, null, message.bytes)
	}

}
