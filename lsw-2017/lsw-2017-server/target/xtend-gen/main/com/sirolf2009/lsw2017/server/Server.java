package com.sirolf2009.lsw2017.server;

import com.esotericsoftware.kryonet.Connection;
import com.sirolf2009.lsw2017.common.model.DBTeam;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class Server implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Connector connector;
  
  private final Database database;
  
  public Server() {
    Connector _connector = new Connector();
    this.connector = _connector;
    Database _database = new Database();
    this.database = _database;
    final Consumer<PointRequest> _function = (PointRequest it) -> {
      Server.log.debug(it);
    };
    this.connector.getFacade().getSubject().subscribeOn(Schedulers.computation()).subscribe(_function);
    final Consumer<PointRequest> _function_1 = (PointRequest it) -> {
      Server.log.debug(it);
      boolean _doesTeamExist = this.database.doesTeamExist(it.getTeamName());
      boolean _not = (!_doesTeamExist);
      if (_not) {
        String _teamName = it.getTeamName();
        String _plus = ("Creating new team: " + _teamName);
        Server.log.debug(_plus);
        this.database.createNewTeam(it.getTeamName());
      }
      String _teamName_1 = it.getTeamName();
      String _plus_1 = ("Awarding " + _teamName_1);
      String _plus_2 = (_plus_1 + " ");
      int _points = it.getPoints();
      String _plus_3 = (_plus_2 + Integer.valueOf(_points));
      String _plus_4 = (_plus_3 + " points");
      Server.log.debug(_plus_4);
      this.database.awardPoints(it);
    };
    this.connector.getFacade().getSubject().subscribeOn(Schedulers.computation()).subscribe(_function_1);
    final Consumer<Pair<PointRequest, DBTeam>> _function_2 = (Pair<PointRequest, DBTeam> it) -> {
      int _points = it.getKey().getPoints();
      String _plus = ((("Awarded " + it.getValue().teamName) + " ") + Integer.valueOf(_points));
      String _plus_1 = (_plus + " points");
      Server.log.info(_plus_1);
      final Function1<Connection, Boolean> _function_3 = (Connection connection) -> {
        int _iD = connection.getID();
        int _clientID = it.getKey().getClientID();
        return Boolean.valueOf((_iD == _clientID));
      };
      final Connection connection = IterableExtensions.<Connection>findFirst(((Iterable<Connection>)Conversions.doWrapArray(this.connector.getServer().getConnections())), _function_3);
      NotifySuccesful _notifySuccesful = new NotifySuccesful(it.getValue().teamName, it.getValue().points);
      connection.sendTCP(_notifySuccesful);
      if (((it.getValue().points % 6) == 0)) {
        Server.log.info((it.getValue().teamName + " is now allowed to go to the battleground"));
        NotifyBattleground _notifyBattleground = new NotifyBattleground(it.getValue().teamName);
        connection.sendTCP(_notifyBattleground);
      }
    };
    this.database.getPointsAwarded().subscribeOn(Schedulers.computation()).subscribe(_function_2);
    final Consumer<Pair<PointRequest, DBTeam>> _function_3 = (Pair<PointRequest, DBTeam> it) -> {
      int _points = it.getKey().getPoints();
      String _plus = ((("Denied " + it.getValue().teamName) + " ") + Integer.valueOf(_points));
      String _plus_1 = (_plus + " points");
      Server.log.info(_plus_1);
      final Function1<Connection, Boolean> _function_4 = (Connection connection) -> {
        int _iD = connection.getID();
        int _clientID = it.getKey().getClientID();
        return Boolean.valueOf((_iD == _clientID));
      };
      final Connection connection = IterableExtensions.<Connection>findFirst(((Iterable<Connection>)Conversions.doWrapArray(this.connector.getServer().getConnections())), _function_4);
      NotifyWait _notifyWait = new NotifyWait(it.getValue().teamName);
      connection.sendTCP(_notifyWait);
    };
    this.database.getPointsDenied().subscribeOn(Schedulers.computation()).subscribe(_function_3);
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
