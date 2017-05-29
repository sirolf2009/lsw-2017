package com.sirolf2009.lsw2017.server

import java.util.regex.Pattern

class PointsParser {

	def static extractNumber(String string, Pattern pattern) {
		return string.extract(pattern).asInt()
	}

	def static extract(String string, Pattern pattern) {
		val matcher = pattern.matcher(string)
		if (matcher.find()) {
			return matcher.group(1)
		} else {
			throw new IllegalArgumentException('''«string» does not contain «pattern.pattern»''')
		}
	}

	def static extractAllNumbers(String string, Pattern pattern) {
		string.extractAll(pattern).map[it.asInt].toList
	}

	def static extractAll(String string, Pattern pattern) {
		val matcher = pattern.matcher(string)
		matcher.find()
		(1 .. matcher.groupCount).map [
			matcher.group(it)
		]
	}

	def static matches(String string, Pattern pattern) {
		val matcher = pattern.matcher(string)
		return matcher.find()
	}

	def static asInt(String string) {
		return Integer.parseInt(string)
	}

}
