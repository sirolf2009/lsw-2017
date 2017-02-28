package com.sirolf2009.lsw2017.common.model;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.EqualsHashCode;
import org.eclipse.xtend.lib.annotations.ToString;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Accessors
@ToString
@EqualsHashCode
@SuppressWarnings("all")
public class PointRequest {
  private String teamName;
  
  private int points;
  
  @Pure
  public String getTeamName() {
    return this.teamName;
  }
  
  public void setTeamName(final String teamName) {
    this.teamName = teamName;
  }
  
  @Pure
  public int getPoints() {
    return this.points;
  }
  
  public void setPoints(final int points) {
    this.points = points;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("teamName", this.teamName);
    b.add("points", this.points);
    return b.toString();
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
    PointRequest other = (PointRequest) obj;
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.teamName== null) ? 0 : this.teamName.hashCode());
    result = prime * result + this.points;
    return result;
  }
}
