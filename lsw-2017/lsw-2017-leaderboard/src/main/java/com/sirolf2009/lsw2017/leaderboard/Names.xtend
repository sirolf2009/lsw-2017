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
		"Britta de aanbiddelijke Sereen",
		"Bruf de aangewaaide Boeven",
		"Kid de koude Colt",
		"Nimzie de achterlijke Bever",
		"Jolt de baldadige Duivel",
		"Skerrie het bange Hulpje",
		"Timeau het behoorlijke Beest",
		"Biersato de Bekende Schreeuwer",
		"Splattie de bekwame Hond",
		"Flekkie de besmettelijke Amoebe",
		"Wibi de onhandige Slager",
		"Choker de bijzondere Komiek",
		"Patty de bittere Ballerina",
		"Kring de grote Pin",
		"Foeh de blije Beer",
		"Bonkletski de brede  Patser",
		"Deffy de brutale Eend",
		"Simplius de complexe Fylosoof",
		"Con Carne de onnozele Gangster",
		"Frits de dakloze Hobbyist",
		"Downy de depressieve Clown",
		"Neat de deskundige Opruimer",
		"Dimm de rare Dokter",
		"Inez de donkere Internetfreak",
		"Dronnie de drukke Dronkaard",
		"Dessie de echte Desperado",
		"Kony de eenzame Koning",
		"Billy de egoistische Bezorger",
		"Uzi de erge Uitbuiter",
		"Chip de ernstige Neuroot",
		"Maya de fantastische Bij",
		"Fenny de felle Vreter",
		"Benny de Boom",
		"Freek de foute Vriend",
		"Haas de frisse Winds",
		"Petromanus de geestelijk  Leider",
		"Loony de gek Toons",
		"Tsjirp de gele Kanarie",
		"Softy de gevoelige Knuppelaar",
		"Kaza de geweldige Tovenaar",
		"Slippy de gladde Nie",
		"Reach de gouden Hand",
		"Gondalf de grijze Duif",
		"Gindalf de groene Parkiet",
		"Steven De grote Dwerg",
		"Klonsie de handige Klusjesman",
		"Metalus de harde Rüst",
		"Lavos de hete Gründ",
		"Irrie de hinderlijke Tand",
		"Sjonny de hopeloze Handelaar",
		"Smiegol de interessante Laar",
		"Buzz de irritante Lightjaar",
		"Juice de jonge Catman",
		"Zuiperman de kale Magrain",
		"Flash de kalme Trockster",
		"Hall het groene Licht",
		"Olivier de blauwe  Schicht",
		"Matilda de koude Pool",
		"Barbara de kwade Orakel",
		"Martian de lage Ster",
		"Vic de lange Arm",
		"Ted de blauwe  Kever",
		"Lex de late Lator",
		"Morpheus de lieve Vlinder",
		"Arend de luie Oog",
		"Mandy de makkelijke Powergirl",
		"Suis de moderne Aquaman",
		"Fikkie de moeilijke Vuurstorm",
		"Alice de mooie Supervrouw",
		"Arnie de negatieve Terminator",
		"Bizarro de nerveuze Braintank",
		"Ben de nieuwe Plasticman",
		"Jonah de normale Heks",
		"Ralph de onbekende Danser",
		"Corrigan de onduidelijke Spectrum",
		"Helena de onmogelijke Jager",
		"Pieter de opvallende Spin",
		"Steve de opvallende Kapitein",
		"Mattie de oude Durfal",
		"Grimm het paarse Ding",
		"Wolverijn de perfecte Chef",
		"Tor de platte Hamer",
		"Hanz de prachtige Nachtkruiper",
		"Magnetnie de praktische Afwijzer",
		"Chad de bronze Surfer",
		"McKooi Het reusachtige Beest",
		"Jobs de rotte Appel",
		"Emma de witte Vriest",
		"Stomp de ijzeren Vuist",
		"Thaneuas de schitterende Bruut",
		"Nick de knuffel Furry",
		"Suzy de spannende Stroom",
		"Colossus de slimme Reus",
		"Steller de smerige Vlieger",
		"Strak de snelle Power",
		"Soe de onzichtbareB96 Per",
		"Sjonnie de menselijke Fakkel",
		"Harrie de technische Kloot",
		"Maggie de zwarte Panter",
		"Jessica de sluwe Speurder",
		"Chill de kille Ijsman",
		"Donnie de verdrietige Gans",
		"Barnes de verkeerde Soldaat",
		"Ronny de ijzeren Man",
		"Azazel de verschrikkelijke Vuur ",
		"Björn de vlugge Baracuda",
		"Blad de nachtloper Blazer",
		"Bruce de pulker Plukker",
		"Bull de dozer Seye",
		"Beng het kanon Chi",
		"Cerebro de werkelijke Denktank",
		"Nie de dokter Doen",
		"Drago de wilde Gekko",
		"Exo de slimme Dus",
		"Elektra de rode Volt",
		"Patrick de zachte Ster",
		"Bob de zenuwachtige Spons",
		"Dank de kikvors Man",
		"Git de groene Goblin",
		"Sling de wapenhandelaar Hit",
		"Jack de rijke Pot",
		"",
		"",
		"",
		"",
		""
	]
	
}