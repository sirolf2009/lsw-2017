package com.sirolf2009.lsw2017.client.net

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.rmi.ObjectSpace
import com.sirolf2009.lsw2017.common.Network
import com.sirolf2009.lsw2017.common.ServerProxy
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors

class Connector {

	static val Logger log = LogManager.getLogger()

	val Client client
	@Accessors var ServerProxy proxy
	@Accessors val SimpleBooleanProperty connected

	new() {
		connected = new SimpleBooleanProperty(false)
		client = new Client()
		client.start()
		Network.register(client)
		client.addListener(new Listener() {
			override connected(Connection arg0) {
				log.info("connected")
				Platform.runLater [
					connected.set(true)
				]
			}

			override disconnected(Connection arg0) {
				log.warn("disconnected")
				Platform.runLater [
					connected.set(false)
				]
				connect()
			}
		})
		connect()
	}

	def connect() {
		new Thread [
			while(!client.connected) {
				try {
					client.connect(5000, "localhost", 1234)
					proxy = ObjectSpace.getRemoteObject(client, 1, ServerProxy)
				} catch(Exception e) {
					log.error("Failed to connect", e)
					Thread.sleep(1000)
				}
			}
		].start()
	}

}
