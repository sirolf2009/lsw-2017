package com.sirolf2009.lsw2017.server;

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
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
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
      String _get = this.acceptedQueues.get(it.getKey().getHostName());
      String _teamName = it.getKey().getTeamName();
      int _points_1 = it.getKey().getPoints();
      NotifySuccesful _notifySuccesful = new NotifySuccesful(_teamName, _points_1);
      this.connector.send(_get, _notifySuccesful);
      if (((it.getValue().timesCheckedIn % 6) == 0)) {
        Server.log.info((it.getValue().teamName + " is now allowed to go to the battleground"));
        String _get_1 = this.battlegroundQueues.get(it.getKey().getHostName());
        NotifyBattleground _notifyBattleground = new NotifyBattleground(it.getValue().teamName);
        this.connector.send(_get_1, _notifyBattleground);
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
  
  public Object moveTeamToBattleground(final Pair<PointRequest, DBTeam> team) {
    return null;
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
