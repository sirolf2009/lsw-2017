package com.sirolf2009.lsw2017.client.net

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.rmi.ObjectSpace
import com.github.plushaze.traynotification.notification.Notifications
import com.github.plushaze.traynotification.notification.TrayNotification
import com.sirolf2009.lsw2017.common.Network
import com.sirolf2009.lsw2017.common.ServerProxy
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.common.model.NotifyWait
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.util.Duration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors

class Connector {

	static val Logger log = LogManager.getLogger()

	@Accessors val Client client
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

			override received(Connection connection, Object packet) {
				if(packet instanceof NotifySuccesful) {
					val succes = packet as NotifySuccesful
					Platform.runLater [
						val notification = new TrayNotification()
						notification.title = "Success"
						notification.message = succes.teamName + " now has " + succes.points + " points"
						notification.notification = Notifications.SUCCESS
						notification.showAndDismiss(Duration.seconds(1))
					]
				} else if(packet instanceof NotifyBattleground) {
					val battleground = packet as NotifyBattleground
					Platform.runLater [
						val notification = new TrayNotification()
						notification.title = "Battleground"
						notification.message = battleground.teamName + " must now go to the battleground"
						notification.notification = Notifications.NOTICE
						notification.showAndDismiss(Duration.seconds(1))
					]
				} else if(packet instanceof NotifyWait) {
					val wait = packet as NotifyWait
					Platform.runLater [
						val notification = new TrayNotification()
						notification.title = "Denied!"
						notification.message = wait.teamName + " must wait a little while longer before scanning"
						notification.notification = Notifications.ERROR
						notification.showAndDismiss(Duration.seconds(1))
					]
				}
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
