package com.sirolf2009.lsw2017.common.model

import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table
import java.util.Date
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.EqualsHashCode
import org.eclipse.xtend.lib.annotations.ToString

@Accessors @ToString @EqualsHashCode @Table(keyspace="lsw2017", name="teams") class DBTeam {
	
	@PartitionKey
	public String teamName
	public int teamNumber
	public int points
	public Date lastCheckedIn
	public int timesCheckedIn
	
}