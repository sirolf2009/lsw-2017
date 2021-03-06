package com.sirolf2009.lsw2017.server.net;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
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
  
  @Accessors
  private final PublishSubject<Pair<PointRequest, DBTeam>> battlegroundAwarded;
  
  public Database() {
    this.cluster = Cluster.builder().addContactPoints("localhost").withPort(32769).build();
    this.session = this.cluster.connect("lsw2017");
    final MappingManager manager = new MappingManager(this.session);
    this.mapperTeam = manager.<DBTeam>mapper(DBTeam.class);
    this.mapperQueue = manager.<DBQueue>mapper(DBQueue.class);
    this.pointsAwarded = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    this.pointsDenied = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    this.battlegroundAwarded = PublishSubject.<Pair<PointRequest, DBTeam>>create();
    Database.log.info("Database connection initialized");
  }
  
  public List<Integer> getIdleBattlegroundsForTeam(final DBTeam team) {
    final List<Integer> battlegrounds = IterableExtensions.<Integer>toList(team.calculateUnplayedBattlegrounds());
    final List<DBQueue> queues = this.getAllQueues();
    final Function1<Integer, Boolean> _function = (Integer interestingBattleground) -> {
      final Function1<DBQueue, Boolean> _function_1 = (DBQueue it) -> {
        return Boolean.valueOf(Integer.valueOf(it.getBattleground()).equals(interestingBattleground));
      };
      int _size = IterableExtensions.size(IterableExtensions.<DBQueue>filter(queues, _function_1));
      return Boolean.valueOf((_size == 0));
    };
    return IterableExtensions.<Integer>toList(IterableExtensions.<Integer>filter(battlegrounds, _function));
  }
  
  public List<DBQueue> getJoinableQueuesForTeam(final DBTeam team) {
    final List<Integer> battlegrounds = IterableExtensions.<Integer>toList(team.calculateUnplayedBattlegrounds());
    final Function1<DBQueue, Boolean> _function = (DBQueue it) -> {
      String _second_battler = it.getSecond_battler();
      return Boolean.valueOf((_second_battler == null));
    };
    final List<DBQueue> queues = IterableExtensions.<DBQueue>toList(IterableExtensions.<DBQueue>filter(this.getAllQueues(), _function));
    final Function1<DBQueue, Boolean> _function_1 = (DBQueue it) -> {
      return Boolean.valueOf(battlegrounds.contains(Integer.valueOf(it.getBattleground())));
    };
    return IterableExtensions.<DBQueue>toList(IterableExtensions.<DBQueue>filter(queues, _function_1));
  }
  
  public Integer getSmallestQueueForTeam(final DBTeam team) {
    final List<Integer> battlegrounds = IterableExtensions.<Integer>toList(team.calculateUnplayedBattlegrounds());
    final List<DBQueue> queues = this.getAllQueues();
    final Function1<DBQueue, Boolean> _function = (DBQueue it) -> {
      return Boolean.valueOf(battlegrounds.contains(Integer.valueOf(it.getBattleground())));
    };
    final Function1<DBQueue, Integer> _function_1 = (DBQueue it) -> {
      return Integer.valueOf(it.getBattleground());
    };
    final Comparator<Map.Entry<Integer, List<DBQueue>>> _function_2 = (Map.Entry<Integer, List<DBQueue>> a, Map.Entry<Integer, List<DBQueue>> b) -> {
      return Integer.valueOf(a.getValue().size()).compareTo(Integer.valueOf(b.getValue().size()));
    };
    return IterableExtensions.<Integer, DBQueue>groupBy(IterableExtensions.<DBQueue>filter(queues, _function), _function_1).entrySet().stream().sorted(_function_2).findFirst().get().getKey();
  }
  
  public void addTeamToQueue(final DBQueue queue, final DBTeam team) {
    queue.setSecond_battler(team.teamName);
    Date _date = new Date();
    queue.setSecond_joined(_date);
    this.mapperQueue.save(queue);
  }
  
  public List<DBQueue> getAllQueues() {
    return this.mapperQueue.map(this.session.execute("SELECT * FROM lsw2017.queue")).all();
  }
  
  public void addTeamToBattleground(final int battleground, final DBTeam team) {
    DBQueue _dBQueue = new DBQueue();
    final Procedure1<DBQueue> _function = (DBQueue queue) -> {
      queue.setBattleground(battleground);
      queue.setFirst_battler(team.teamName);
      Date _date = new Date();
      queue.setFirst_joined(_date);
    };
    DBQueue _doubleArrow = ObjectExtensions.<DBQueue>operator_doubleArrow(_dBQueue, _function);
    this.mapperQueue.save(_doubleArrow);
  }
  
  public ResultSet finishBattle(final int battleground, final String team) {
    ResultSet _xblockexpression = null;
    {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("DELETE FROM lsw2017.queue where battleground=");
      _builder.append(battleground);
      _builder.append(" and first_battler=\'");
      _builder.append(team);
      _builder.append("\' ");
      this.session.execute(_builder.toString());
      final Function1<DBQueue, Boolean> _function = (DBQueue queue) -> {
        return Boolean.valueOf((((queue.getBattleground() == battleground) && (queue.getSecond_battler() != null)) && queue.getSecond_battler().equals(team)));
      };
      final Consumer<DBQueue> _function_1 = (DBQueue it) -> {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("DELETE FROM lsw2017.queue where battleground=");
        _builder_1.append(battleground);
        _builder_1.append(" and first_battler=\'");
        String _first_battler = it.getFirst_battler();
        _builder_1.append(_first_battler);
        _builder_1.append("\' ");
        this.session.execute(_builder_1.toString());
      };
      IterableExtensions.<DBQueue>filter(this.getAllQueues(), _function).forEach(_function_1);
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("UPDATE lsw2017.teams SET battleground");
      _builder_1.append(battleground);
      _builder_1.append("=true WHERE teamname=\'");
      _builder_1.append(team);
      _builder_1.append("\' ");
      _xblockexpression = this.session.execute(_builder_1.toString());
    }
    return _xblockexpression;
  }
  
  public DBTeam awardBattlegroundPoints(final PointRequest request, final int battleground, final int points) {
    DBTeam _get = this.mapperTeam.get(request.getTeamName());
    final Procedure1<DBTeam> _function = (DBTeam it) -> {
      int _points = it.points;
      it.points = (_points + points);
      this.save(it);
      this.finishBattle(battleground, request.getTeamName());
      Pair<PointRequest, DBTeam> _mappedTo = Pair.<PointRequest, DBTeam>of(request, it);
      this.battlegroundAwarded.onNext(_mappedTo);
    };
    return ObjectExtensions.<DBTeam>operator_doubleArrow(_get, _function);
  }
  
  public DBTeam awardPoints(final PointRequest request, final int points) {
    DBTeam _get = this.mapperTeam.get(request.getTeamName());
    final Procedure1<DBTeam> _function = (DBTeam it) -> {
      long _currentTime = request.getCurrentTime();
      long _time = it.lastCheckedIn.getTime();
      long _minus = (_currentTime - _time);
      long _millis = Duration.ofMinutes(0).toMillis();
      boolean _greaterThan = (_minus > _millis);
      if (_greaterThan) {
        int _points = it.points;
        it.points = (_points + points);
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
  
  @Pure
  public PublishSubject<Pair<PointRequest, DBTeam>> getBattlegroundAwarded() {
    return this.battlegroundAwarded;
  }
}
