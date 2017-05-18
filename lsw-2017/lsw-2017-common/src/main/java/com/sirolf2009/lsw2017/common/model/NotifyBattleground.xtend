package com.sirolf2009.lsw2017.common.model

import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class NotifyBattleground {
	
	var String teamName
	var int battleground
	
	new() {}
	
	new(String teamName, int battleground) {
		this.teamName = teamName
		this.battleground = battleground
	}
	
}