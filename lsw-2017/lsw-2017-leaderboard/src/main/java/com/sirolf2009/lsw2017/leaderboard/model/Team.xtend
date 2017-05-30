package com.sirolf2009.lsw2017.leaderboard.model

import xtendfx.beans.FXBindable
import org.eclipse.xtend.lib.annotations.ToString
import org.eclipse.xtend.lib.annotations.EqualsHashCode
import org.eclipse.xtend.lib.annotations.Accessors

@FXBindable @ToString @EqualsHashCode @Accessors class Team {
	
	String team
	String name
	int likes
	String subcamp
	long lastCheckedIn
	
	new(String team, String name, int likes, String subcamp, long lastCheckedIn) {
		this.team = team
		this.name = name
		this.likes = likes
		this.subcamp = subcamp
		this.lastCheckedIn = lastCheckedIn
	}
	
}