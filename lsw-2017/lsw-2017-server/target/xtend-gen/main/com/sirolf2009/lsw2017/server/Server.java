package com.sirolf2009.lsw2017.server;

import com.sirolf2009.lsw2017.common.model.DBQueue;
import com.sirolf2009.lsw2017.common.model.DBTeam;
import com.sirolf2009.lsw2017.common.model.Handshake;
import com.sirolf2009.lsw2017.common.model.NotifyBattleground;
import com.sirolf2009.lsw2017.common.model.NotifySuccesful;
import com.sirolf2009.lsw2017.common.model.NotifyWait;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import com.sirolf2009.lsw2017.server.net.Connector;
import com.sirolf2009.lsw2017.server.net.Database;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class Server implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Connector connector;
  
  private final Database database;
  
  private final HashMap<String, String> acceptedQueues = new HashMap<String, String>();
  
  private final HashMap<String, String> deniedQueues = new HashMap<String, String>();
  
  private final HashMap<String, String> battlegroundQueues = new HashMap<String, String>();
  
  public Server() {
    Connector _connector = new Connector();
    this.connector = _connector;
    Database _database = new Database();
    this.database = _database;
    final Consumer<Handshake> _function = (Handshake it) -> {
      String _plus = (it + " connected");
      Server.log.info(_plus);
    };
    this.connector.getConnected().subscribe(_function);
    final Consumer<Handshake> _function_1 = (Handshake it) -> {
      this.acceptedQueues.put(it.getName(), it.getAcceptedQueue());
    };
    this.connector.getConnected().subscribe(_function_1);
    final Consumer<Handshake> _function_2 = (Handshake it) -> {
      this.deniedQueues.put(it.getName(), it.getDeniedQueue());
    };
    this.connector.getConnected().subscribe(_function_2);
    final Consumer<Handshake> _function_3 = (Handshake it) -> {
      this.battlegroundQueues.put(it.getName(), it.getBattlegroundQueue());
    };
    this.connector.getConnected().subscribe(_function_3);
    final Consumer<PointRequest> _function_4 = (PointRequest it) -> {
      Server.log.debug(("Received " + it));
    };
    this.connector.getPointRequest().subscribe(_function_4);
    final Consumer<PointRequest> _function_5 = (PointRequest it) -> {
      boolean _doesTeamExist = this.database.doesTeamExist(it.getTeamName());
      boolean _not = (!_doesTeamExist);
      if (_not) {
        Server.log.info("Creating new team");
        this.database.createNewTeam(it.getTeamName());
      }
      String _teamName = it.getTeamName();
      String _plus = ("Awarding " + _teamName);
      String _plus_1 = (_plus + " ");
      int _points = it.getPoints();
      String _plus_2 = (_plus_1 + Integer.valueOf(_points));
      String _plus_3 = (_plus_2 + " points");
      Server.log.debug(_plus_3);
      this.database.awardPoints(it);
    };
    this.connector.getPointRequest().subscribeOn(Schedulers.computation()).subscribe(_function_5);
    final Consumer<Pair<PointRequest, DBTeam>> _function_6 = (Pair<PointRequest, DBTeam> it) -> {
      int _points = it.getKey().getPoints();
      String _plus = ((("Awarded " + it.getValue().teamName) + " ") + Integer.valueOf(_points));
      String _plus_1 = (_plus + " points from ");
      String _hostName = it.getKey().getHostName();
      String _plus_2 = (_plus_1 + _hostName);
      Server.log.info(_plus_2);
      if (((it.getValue().timesCheckedIn % 2) == 0)) {
        Server.log.info((it.getValue().teamName + " is now allowed to go to the battleground"));
        String _get = this.battlegroundQueues.get(it.getKey().getHostName());
        int _moveTeamToBattleground = this.moveTeamToBattleground(it);
        NotifyBattleground _notifyBattleground = new NotifyBattleground(it.getValue().teamName, _moveTeamToBattleground);
        this.connector.send(_get, _notifyBattleground);
      } else {
        String _get_1 = this.acceptedQueues.get(it.getKey().getHostName());
        String _teamName = it.getKey().getTeamName();
        int _points_1 = it.getKey().getPoints();
        NotifySuccesful _notifySuccesful = new NotifySuccesful(_teamName, _points_1);
        this.connector.send(_get_1, _notifySuccesful);
      }
    };
    this.database.getPointsAwarded().subscribeOn(Schedulers.io()).subscribe(_function_6);
    final Consumer<Pair<PointRequest, DBTeam>> _function_7 = (Pair<PointRequest, DBTeam> it) -> {
      int _points = it.getKey().getPoints();
      String _plus = ((("Denied " + it.getValue().teamName) + " ") + Integer.valueOf(_points));
      String _plus_1 = (_plus + " points from ");
      String _hostName = it.getKey().getHostName();
      String _plus_2 = (_plus_1 + _hostName);
      Server.log.info(_plus_2);
      String _get = this.deniedQueues.get(it.getKey().getHostName());
      String _teamName = it.getKey().getTeamName();
      NotifyWait _notifyWait = new NotifyWait(_teamName);
      this.connector.send(_get, _notifyWait);
    };
    this.database.getPointsDenied().subscribeOn(Schedulers.io()).subscribe(_function_7);
  }
  
  public int moveTeamToBattleground(final Pair<PointRequest, DBTeam> team) {
    final List<DBQueue> queues = this.database.getJoinableQueuesForTeam(team.getValue());
    final Function1<DBQueue, Integer> _function = (DBQueue it) -> {
      return Integer.valueOf(it.getBattleground());
    };
    final Predicate<Map.Entry<Integer, List<DBQueue>>> _function_1 = (Map.Entry<Integer, List<DBQueue>> it) -> {
      int _size = it.getValue().size();
      return (_size == 1);
    };
    final Optional<Map.Entry<Integer, List<DBQueue>>> joinableEmpty = IterableExtensions.<Integer, DBQueue>groupBy(queues, _function).entrySet().stream().filter(_function_1).findFirst();
    boolean _isPresent = joinableEmpty.isPresent();
    if (_isPresent) {
      final DBQueue battleground = joinableEmpty.get().getValue().get(0);
      this.database.addTeamToQueue(battleground, team.getValue());
      return battleground.getBattleground();
    }
    final List<Integer> idle = this.database.getIdleBattlegroundsForTeam(team.getValue());
    int _size = idle.size();
    boolean _greaterThan = (_size > 0);
    if (_greaterThan) {
      final Integer battleground_1 = idle.get(0);
      this.database.addTeamToBattleground((battleground_1).intValue(), team.getValue());
      return (battleground_1).intValue();
    } else {
      final Function1<DBQueue, Integer> _function_2 = (DBQueue it) -> {
        return Integer.valueOf(it.getBattleground());
      };
      final Comparator<Map.Entry<Integer, List<DBQueue>>> _function_3 = (Map.Entry<Integer, List<DBQueue>> a, Map.Entry<Integer, List<DBQueue>> b) -> {
        return Integer.valueOf(a.getValue().size()).compareTo(Integer.valueOf(b.getValue().size()));
      };
      final Optional<Map.Entry<Integer, List<DBQueue>>> joinable = IterableExtensions.<Integer, DBQueue>groupBy(queues, _function_2).entrySet().stream().sorted(_function_3).findFirst();
      boolean _isPresent_1 = joinable.isPresent();
      if (_isPresent_1) {
        final DBQueue battleground_2 = joinable.get().getValue().get(0);
        this.database.addTeamToQueue(battleground_2, team.getValue());
        return battleground_2.getBattleground();
      } else {
        final Integer battleground_3 = this.database.getSmallestQueueForTeam(team.getValue());
        this.database.addTeamToBattleground((battleground_3).intValue(), team.getValue());
        return (battleground_3).intValue();
      }
    }
  }
  
  @Override
  public void close() throws IOException {
    this.database.close();
  }
  
  public static void main(final String[] ars) {
    try {
      Server server = null;
      try {
        Server _server = new Server();
        server = _server;
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          Server.log.fatal("Server exited with an error", e);
          server.close();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
