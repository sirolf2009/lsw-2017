package com.sirolf2009.lsw2017.server.net

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.sirolf2009.lsw2017.common.Network
import java.util.Arrays
import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class Connector {

	static val Logger log = LogManager.getLogger()

	val Facade facade
	val Server server

	new() {
		val configProperties = new Properties()
		configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "digital:2181")
		configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.name)
		configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.name)
		configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "connected-consumer")
		configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple")
		
		println("listening")
		val consumer  = new KafkaConsumer<String, String>(configProperties);
        consumer.subscribe(Arrays.asList("connected"));
		println("listening")
        val records = consumer.poll(10000);
		println("received")
        records.forEach[println(it)]

		this.facade = new Facade()
		this.server = new Server() {
			override protected newConnection() {
				return facade
			}
		}
		Network.register(server)
		server.start()
		server.addListener(new Listener() {
			override connected(Connection connection) {
				connection.name = connection.remoteAddressTCP + ""
				log.info(connection + " connected")
			}

			override disconnected(Connection connection) {
				log.info(connection + " disconnected")
			}
		})
		server.bind(1234)
		log.info("Connector initialized")
	}

}
