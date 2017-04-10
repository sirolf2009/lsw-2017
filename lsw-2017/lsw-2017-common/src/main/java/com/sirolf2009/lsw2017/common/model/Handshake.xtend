package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.ToString

@Accessors @ToString class Handshake {

	var String name
	var String acceptedQueue
	var String deniedQueue
	var String battlegroundQueue
	
	new() {}
	
	new(String name, String acceptedQueue, String deniedQueue, String battlegroundQueue) {
		this.name = name
		this.acceptedQueue = acceptedQueue
		this.deniedQueue = deniedQueue
		this.battlegroundQueue = battlegroundQueue
	}
	
}