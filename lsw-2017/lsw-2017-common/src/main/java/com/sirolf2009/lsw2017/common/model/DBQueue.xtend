package com.sirolf2009.lsw2017.common.model

import com.datastax.driver.mapping.annotations.Table
import java.util.Date
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.EqualsHashCode
import org.eclipse.xtend.lib.annotations.ToString

@Accessors @ToString @EqualsHashCode @Table(keyspace="lsw2017", name="teams") class DBQueue {
	
	private var int battleground
	private var String first_battler
	private var String second_battler
	private var Date first_joined
	private var Date second_joined
	private var int queue_position
	
}
