package com.sirolf2009.lsw2017.leaderboard

import java.util.Optional
import java.util.regex.Pattern

class Names {
	
	static val teamnamePattern = Pattern.compile("ploeg-([0-9]+)")
	
	def static getSubkamp(String teamName) {
		return teamName.teamNumber.map[subkamp.orElse("Onbekend")].orElse("Onbekend")
	}

	def static getSubkamp(int teamNumber) {
		if (teamNumber > 0 && teamNumber <= 30) {
			return Optional.of("1")
		} else if (teamNumber > 30 && teamNumber <= 60) {
			return Optional.of("2")
		} else if (teamNumber > 60 && teamNumber <= 90) {
			return Optional.of("3")
		} else if (teamNumber > 90) {
			return Optional.of("4")
		} else {
			return Optional.empty()
		}
	}
	
	def static getVillainName(String teamName) {
		return teamName.teamNumber.map[villainName].orElse("Onbekend")
	}
	
	def static getVillainName(int teamNumber) {
		return names.get(teamNumber)
	}
	
	def static getTeamNumber(String teamName) {
		val it = teamnamePattern.matcher(teamName)
		if (find()) {
			return Optional.of(Integer.parseInt(group(1)))
		} else {
			return Optional.empty()
		}
	}
	
	static val names = #[
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER",
		"PLACEHOLDER"
	]
	
}