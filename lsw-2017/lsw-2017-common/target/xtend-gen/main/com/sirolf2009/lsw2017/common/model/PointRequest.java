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
  private String hostName;
  
  private String teamName;
  
  private String points;
  
  private long currentTime;
  
  public PointRequest() {
  }
  
  public PointRequest(final String hostName, final String teamName, final String points, final long currentTime) {
    this.hostName = hostName;
    this.teamName = teamName;
    this.points = points;
    this.currentTime = currentTime;
  }
  
  @Pure
  public String getHostName() {
    return this.hostName;
  }
  
  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }
  
  @Pure
  public String getTeamName() {
    return this.teamName;
  }
  
  public void setTeamName(final String teamName) {
    this.teamName = teamName;
  }
  
  @Pure
  public String getPoints() {
    return this.points;
  }
  
  public void setPoints(final String points) {
    this.points = points;
  }
  
  @Pure
  public long getCurrentTime() {
    return this.currentTime;
  }
  
  public void setCurrentTime(final long currentTime) {
    this.currentTime = currentTime;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("hostName", this.hostName);
    b.add("teamName", this.teamName);
    b.add("points", this.points);
    b.add("currentTime", this.currentTime);
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
    if (this.hostName == null) {
      if (other.hostName != null)
        return false;
    } else if (!this.hostName.equals(other.hostName))
      return false;
    if (this.teamName == null) {
      if (other.teamName != null)
        return false;
    } else if (!this.teamName.equals(other.teamName))
      return false;
    if (this.points == null) {
      if (other.points != null)
        return false;
    } else if (!this.points.equals(other.points))
      return false;
    if (other.currentTime != this.currentTime)
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.hostName== null) ? 0 : this.hostName.hashCode());
    result = prime * result + ((this.teamName== null) ? 0 : this.teamName.hashCode());
    result = prime * result + ((this.points== null) ? 0 : this.points.hashCode());
    result = prime * result + (int) (this.currentTime ^ (this.currentTime >>> 32));
    return result;
  }
}
