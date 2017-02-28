package com.sirolf2009.lsw2017.server.net

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.sirolf2009.lsw2017.common.Network
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class Connector {
	
	static val Logger log = LogManager.getLogger()
	
	val Facade facade
	val Server server
	
	new() {
		this.facade = new Facade()
		this.server = new Server() {
			override protected newConnection() {
				return facade
			}
		}
		Network.register(server)
		server.start()
		server.addListener(new Listener() {
			override connected(Connection arg0) {
				log.info(arg0.endPoint +" connecting")
			}
		})
		server.bind(1234)
		log.info("Connector initialized")
	}
	
}