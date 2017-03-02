package com.sirolf2009.lsw2017.leaderboard.model

import xtendfx.beans.FXBindable

@FXBindable class Team {
	
	int likes
	String name
	String subcamp
	long lastCheckedIn
	
	new(int likes, String name, String subcamp, long lastCheckedIn) {
		this.likes = likes
		this.name = name
		this.subcamp = subcamp
		this.lastCheckedIn = lastCheckedIn
	}
	
}