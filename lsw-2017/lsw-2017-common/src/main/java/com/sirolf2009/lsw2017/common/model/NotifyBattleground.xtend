package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class NotifyBattleground {
	
	var  String teamName
	
	new() {}
	
	new(String teamName) {
		this.teamName = teamName
	}
	
}