package com.sirolf2009.lsw2017.server.net;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.sirolf2009.lsw2017.common.model.DBTeam;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import io.reactivex.subjects.PublishSubject;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Database implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Cluster cluster;
  
  private final Session session;
  
  private final Mapper<DBTeam> mapper;
  
  @Accessors
  private final PublishSubject<Pair<PointRequest, DBTeam>> pointsAwarded;
  
  @Accessors
  private final PublishSubject<Pair<PointRequest, DBTeam>> pointsDenied;
  
  public Database() {
    this.cluster = Cluster.builder().addContactPoints("localhost").build();
    this.session = this.cluster.connect("lsw2017");
    final MappingManager manager = new MappingManager(this.session);
    this.mapper = manager.<DBTeam>mapper(DBTeam.class);
    this.pointsAwarded = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    this.pointsDenied = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    Database.log.info("Database connection initialized");
  }
  
  public DBTeam awardPoints(final PointRequest request) {
    DBTeam _get = this.mapper.get(request.getTeamName());
    final Procedure1<DBTeam> _function = (DBTeam it) -> {
      long _currentTime = request.getCurrentTime();
      long _time = it.lastCheckedIn.getTime();
      long _minus = (_currentTime - _time);
      long _millis = Duration.ofMinutes(1).toMillis();
      boolean _greaterThan = (_minus > _millis);
      if (_greaterThan) {
        int _points = it.points;
        int _points_1 = request.getPoints();
        it.points = (_points + _points_1);
        long _currentTime_1 = request.getCurrentTime();
        Date _date = new Date(_currentTime_1);
        it.lastCheckedIn = _date;
        it.timesCheckedIn++;
        this.save(it);
        Pair<PointRequest, DBTeam> _mappedTo = Pair.<PointRequest, DBTeam>of(request, it);
        this.pointsAwarded.onNext(_mappedTo);
      } else {
        Pair<PointRequest, DBTeam> _mappedTo_1 = Pair.<PointRequest, DBTeam>of(request, it);
        this.pointsDenied.onNext(_mappedTo_1);
      }
    };
    return ObjectExtensions.<DBTeam>operator_doubleArrow(_get, _function);
  }
  
  public int getPoints(final String teamName) {
    return this.getTeam(teamName).points;
  }
  
  public void createNewTeam(final String teamName) {
    DBTeam _dBTeam = new DBTeam();
    final Procedure1<DBTeam> _function = (DBTeam it) -> {
      it.teamName = teamName;
      it.teamNumber = 0;
      Date _date = new Date(0);
      it.lastCheckedIn = _date;
      it.points = 0;
      it.timesCheckedIn = 0;
    };
    final DBTeam team = ObjectExtensions.<DBTeam>operator_doubleArrow(_dBTeam, _function);
    this.save(team);
  }
  
  public boolean doesTeamExist(final String teamName) {
    DBTeam _team = this.getTeam(teamName);
    return (_team != null);
  }
  
  public DBTeam getTeam(final String teamName) {
    return this.mapper.get(teamName);
  }
  
  public void save(final DBTeam team) {
    this.mapper.save(team);
  }
  
  public ListenableFuture<Void> saveAsync(final DBTeam team) {
    return this.mapper.saveAsync(team);
  }
  
  @Override
  public void close() throws IOException {
    this.cluster.close();
  }
  
  @Pure
  public PublishSubject<Pair<PointRequest, DBTeam>> getPointsAwarded() {
    return this.pointsAwarded;
  }
  
  @Pure
  public PublishSubject<Pair<PointRequest, DBTeam>> getPointsDenied() {
    return this.pointsDenied;
  }
}
