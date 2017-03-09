package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.ToString
import org.eclipse.xtend.lib.annotations.EqualsHashCode

@Accessors @ToString @EqualsHashCode class PointRequest {
	
	int clientID
	String teamName
	int points
	long currentTime
	
	new() {}
	
	new(int clientID, String teamName, int points, long currentTime) {
		this.clientID = clientID
		this.teamName = teamName
		this.points = points
		this.currentTime = currentTime
	}
	
}
