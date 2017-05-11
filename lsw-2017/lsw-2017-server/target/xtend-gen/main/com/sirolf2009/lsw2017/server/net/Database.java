package com.sirolf2009.lsw2017.server.net;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.sirolf2009.lsw2017.common.model.DBQueue;
import com.sirolf2009.lsw2017.common.model.DBTeam;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import io.reactivex.subjects.PublishSubject;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Database implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Cluster cluster;
  
  private final Session session;
  
  private final Mapper<DBTeam> mapperTeam;
  
  private final Mapper<DBQueue> mapperQueue;
  
  @Accessors
  private final PublishSubject<Pair<PointRequest, DBTeam>> pointsAwarded;
  
  @Accessors
  private final PublishSubject<Pair<PointRequest, DBTeam>> pointsDenied;
  
  public Database() {
    this.cluster = Cluster.builder().addContactPoints("localhost").withPort(32769).build();
    this.session = this.cluster.connect("lsw2017");
    final MappingManager manager = new MappingManager(this.session);
    this.mapperTeam = manager.<DBTeam>mapper(DBTeam.class);
    this.mapperQueue = manager.<DBQueue>mapper(DBQueue.class);
    this.pointsAwarded = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    this.pointsDenied = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    Database.log.info("Database connection initialized");
  }
  
  public List<DBQueue> getQueues() {
    return this.mapperQueue.map(this.session.execute("SELECT * FROM lsw2017.queue")).all();
  }
  
  public List<DBQueue> getQueuesForTeam(final DBTeam team) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("SELECT * FROM lsw2017.queue WERE battleground in (");
    int _xifexpression = (int) 0;
    if (team.battleground1) {
      _xifexpression = 0;
    } else {
      _xifexpression = 1;
    }
    int _xifexpression_1 = (int) 0;
    if (team.battleground2) {
      _xifexpression_1 = 0;
    } else {
      _xifexpression_1 = 2;
    }
    int _xifexpression_2 = (int) 0;
    if (team.battleground3) {
      _xifexpression_2 = 0;
    } else {
      _xifexpression_2 = 3;
    }
    int _xifexpression_3 = (int) 0;
    if (team.battleground4) {
      _xifexpression_3 = 0;
    } else {
      _xifexpression_3 = 4;
    }
    int _xifexpression_4 = (int) 0;
    if (team.battleground5) {
      _xifexpression_4 = 0;
    } else {
      _xifexpression_4 = 5;
    }
    int _xifexpression_5 = (int) 0;
    if (team.battleground6) {
      _xifexpression_5 = 0;
    } else {
      _xifexpression_5 = 6;
    }
    final Function1<Integer, Boolean> _function = (Integer it) -> {
      return Boolean.valueOf(((it).intValue() != 0));
    };
    final Function1<Integer, String> _function_1 = (Integer it) -> {
      return (it + "");
    };
    final Function2<String, String, String> _function_2 = (String a, String b) -> {
      return ((a + ",") + b);
    };
    String _reduce = IterableExtensions.<String>reduce(IterableExtensions.<Integer, String>map(IterableExtensions.<Integer>filter(Arrays.<Integer>asList(Integer.valueOf(_xifexpression), Integer.valueOf(_xifexpression_1), Integer.valueOf(_xifexpression_2), Integer.valueOf(_xifexpression_3), Integer.valueOf(_xifexpression_4), Integer.valueOf(_xifexpression_5)), _function), _function_1), _function_2);
    _builder.append(_reduce);
    _builder.append(")");
    return this.mapperQueue.map(this.session.execute(_builder.toString())).all();
  }
  
  public DBTeam awardPoints(final PointRequest request) {
    DBTeam _get = this.mapperTeam.get(request.getTeamName());
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
    return this.mapperTeam.get(teamName);
  }
  
  public void save(final DBTeam team) {
    this.mapperTeam.save(team);
  }
  
  public ListenableFuture<Void> saveAsync(final DBTeam team) {
    return this.mapperTeam.saveAsync(team);
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
