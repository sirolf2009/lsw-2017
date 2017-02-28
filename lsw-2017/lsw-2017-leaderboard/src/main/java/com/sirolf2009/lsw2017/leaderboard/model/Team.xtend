package com.sirolf2009.lsw2017.leaderboard.model

import xtendfx.beans.FXBindable

@FXBindable class Team {
	
	int likes
	String name
	String subcamp
	
	new(int likes, String name, String subcamp) {
		this.likes = likes
		this.name = name
		this.subcamp = subcamp
	}
	
}