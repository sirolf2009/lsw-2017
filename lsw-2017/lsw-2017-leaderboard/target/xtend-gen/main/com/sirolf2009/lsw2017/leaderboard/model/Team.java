package com.sirolf2009.lsw2017.leaderboard.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import xtendfx.beans.FXBindable;

@FXBindable
@SuppressWarnings("all")
public class Team {
  public Team(final int likes, final String name, final String subcamp) {
    this.setLikes(likes);
    this.setName(name);
    this.setSubcamp(subcamp);
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
}
