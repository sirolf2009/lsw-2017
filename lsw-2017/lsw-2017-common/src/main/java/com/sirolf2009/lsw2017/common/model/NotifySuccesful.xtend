package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class NotifySuccesful {
	
	var String teamName
	var int points
	
	new() {}
	
	new(String teamName, int points) {
		this.teamName = teamName
		this.points = points
	}
	
}