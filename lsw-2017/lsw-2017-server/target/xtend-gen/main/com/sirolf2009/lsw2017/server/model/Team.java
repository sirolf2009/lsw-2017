package com.sirolf2009.lsw2017.server.model;

import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@Deprecated
@SuppressWarnings("all")
public class Team {
  private final String teamName;
  
  private final int points;
  
  public Team(final String teamName, final int points) {
    super();
    this.teamName = teamName;
    this.points = points;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.teamName== null) ? 0 : this.teamName.hashCode());
    result = prime * result + this.points;
    return result;
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Team other = (Team) obj;
    if (this.teamName == null) {
      if (other.teamName != null)
        return false;
    } else if (!this.teamName.equals(other.teamName))
      return false;
    if (other.points != this.points)
      return false;
    return true;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("teamName", this.teamName);
    b.add("points", this.points);
    return b.toString();
  }
  
  @Pure
  public String getTeamName() {
    return this.teamName;
  }
  
  @Pure
  public int getPoints() {
    return this.points;
  }
}
