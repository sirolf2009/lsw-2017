package com.sirolf2009.lsw2017.common

import com.rabbitmq.client.Channel

class Queues {
	
	public static val CONNECTED = "connected"
	public static val POINT_REQUEST = "pointRequest"
	public static val POINTS_AWARDED = "pointsAwarded"
	public static val POINTS_DENIED = "pointsDenied"
	
	def static declareQueues(Channel channel) {
		channel.queueDeclare(CONNECTED, false, false, false, null)
		channel.queueDeclare(POINT_REQUEST, true, false, false, null)
		channel.queueDeclare(POINTS_AWARDED, true, false, false, null)
	}
	
}