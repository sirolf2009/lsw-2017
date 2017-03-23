package com.sirolf2009.lsw2017.common

import com.esotericsoftware.kryonet.EndPoint
import com.esotericsoftware.kryonet.rmi.ObjectSpace
import com.sirolf2009.lsw2017.common.model.PointRequest
import com.sirolf2009.lsw2017.common.model.NotifySuccesful
import com.sirolf2009.lsw2017.common.model.NotifyBattleground
import com.sirolf2009.lsw2017.common.model.NotifyWait

class Network {
	
	def public static void register(EndPoint endpoint) {
		val kryo = endpoint.kryo
		kryo.register(PointRequest)
		kryo.register(NotifySuccesful)
		kryo.register(NotifyBattleground)
		kryo.register(NotifyWait)
		ObjectSpace.registerClasses(kryo)
		kryo.register(ServerProxy)
	}
	
}