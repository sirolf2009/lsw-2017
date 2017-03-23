package com.sirolf2009.lsw2017.leaderboard.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.EqualsHashCode;
import org.eclipse.xtend.lib.annotations.ToString;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import xtendfx.beans.FXBindable;

@FXBindable
@ToString
@EqualsHashCode
@Accessors
@SuppressWarnings("all")
public class Team {
  public Team(final int likes, final String name, final String subcamp, final long lastCheckedIn) {
    this.setLikes(likes);
    this.setName(name);
    this.setSubcamp(subcamp);
    this.setLastCheckedIn(lastCheckedIn);
  }
  
  private final static int DEFAULT_LIKES = 0;
  
  private SimpleIntegerProperty likesProperty;
  
  public int getLikes() {
    return (this.likesProperty != null)? this.likesProperty.get() : DEFAULT_LIKES;
  }
  
  public void setLikes(final int likes) {
    this.likesProperty().set(likes);
  }
  
  public IntegerProperty likesProperty() {
    if (this.likesProperty == null) { 
    	this.likesProperty = new SimpleIntegerProperty(this, "likes", DEFAULT_LIKES);
    }
    return this.likesProperty;
  }
  
  private final static String DEFAULT_NAME = null;
  
  private SimpleStringProperty nameProperty;
  
  public String getName() {
    return (this.nameProperty != null)? this.nameProperty.get() : DEFAULT_NAME;
  }
  
  public void setName(final String name) {
    this.nameProperty().set(name);
  }
  
  public StringProperty nameProperty() {
    if (this.nameProperty == null) { 
    	this.nameProperty = new SimpleStringProperty(this, "name", DEFAULT_NAME);
    }
    return this.nameProperty;
  }
  
  private final static String DEFAULT_SUBCAMP = null;
  
  private SimpleStringProperty subcampProperty;
  
  public String getSubcamp() {
    return (this.subcampProperty != null)? this.subcampProperty.get() : DEFAULT_SUBCAMP;
  }
  
  public void setSubcamp(final String subcamp) {
    this.subcampProperty().set(subcamp);
  }
  
  public StringProperty subcampProperty() {
    if (this.subcampProperty == null) { 
    	this.subcampProperty = new SimpleStringProperty(this, "subcamp", DEFAULT_SUBCAMP);
    }
    return this.subcampProperty;
  }
  
  private final static long DEFAULT_LASTCHECKEDIN = 0;
  
  private SimpleLongProperty lastCheckedInProperty;
  
  public long getLastCheckedIn() {
    return (this.lastCheckedInProperty != null)? this.lastCheckedInProperty.get() : DEFAULT_LASTCHECKEDIN;
  }
  
  public void setLastCheckedIn(final long lastCheckedIn) {
    this.lastCheckedInProperty().set(lastCheckedIn);
  }
  
  public LongProperty lastCheckedInProperty() {
    if (this.lastCheckedInProperty == null) { 
    	this.lastCheckedInProperty = new SimpleLongProperty(this, "lastCheckedIn", DEFAULT_LASTCHECKEDIN);
    }
    return this.lastCheckedInProperty;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
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
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    int result = 1;
    return result;
  }
}
