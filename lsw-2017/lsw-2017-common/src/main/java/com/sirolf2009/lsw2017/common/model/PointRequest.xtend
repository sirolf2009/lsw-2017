package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.ToString
import org.eclipse.xtend.lib.annotations.EqualsHashCode

@Accessors @ToString @EqualsHashCode class PointRequest {
	
	String hostName
	String teamName
	int points
	long currentTime
	
	new() {}
	
	new(String hostName, String teamName, int points, long currentTime) {
		this.hostName = hostName
		this.teamName = teamName
		this.points = points
		this.currentTime = currentTime
	}
	
}
