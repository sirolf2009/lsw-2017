package com.sirolf2009.lsw2017.server;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.esotericsoftware.kryonet.Connection;
import com.sirolf2009.lsw2017.common.model.NotifyBattleground;
import com.sirolf2009.lsw2017.common.model.NotifySuccesful;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import com.sirolf2009.lsw2017.server.net.Connector;
import com.sirolf2009.lsw2017.server.net.Database;
import com.sirolf2009.lsw2017.server.net.Facade;
import java.io.Closeable;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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
    Facade _facade = this.connector.getFacade();
    PublishSubject<PointRequest> _subject = _facade.getSubject();
    Scheduler _computation = Schedulers.computation();
    Observable<PointRequest> _subscribeOn = _subject.subscribeOn(_computation);
    final Action1<PointRequest> _function = (PointRequest it) -> {
      Server.log.debug(it);
    };
    _subscribeOn.subscribe(_function);
    Facade _facade_1 = this.connector.getFacade();
    PublishSubject<PointRequest> _subject_1 = _facade_1.getSubject();
    Scheduler _computation_1 = Schedulers.computation();
    Observable<PointRequest> _subscribeOn_1 = _subject_1.subscribeOn(_computation_1);
    final Action1<PointRequest> _function_1 = (PointRequest it) -> {
      String _teamName = it.getTeamName();
      boolean _doesTeamExist = this.database.doesTeamExist(_teamName);
      boolean _not = (!_doesTeamExist);
      if (_not) {
        String _teamName_1 = it.getTeamName();
        String _plus = ("Creating new team: " + _teamName_1);
        Server.log.debug(_plus);
        String _teamName_2 = it.getTeamName();
        this.database.createNewTeam(_teamName_2);
      }
      String _teamName_3 = it.getTeamName();
      String _plus_1 = ("Awarding " + _teamName_3);
      String _plus_2 = (_plus_1 + " ");
      int _points = it.getPoints();
      String _plus_3 = (_plus_2 + Integer.valueOf(_points));
      String _plus_4 = (_plus_3 + " points");
      Server.log.debug(_plus_4);
      this.database.awardPoints(it);
      String _teamName_4 = it.getTeamName();
      String _plus_5 = ("Awarded " + _teamName_4);
      String _plus_6 = (_plus_5 + " ");
      int _points_1 = it.getPoints();
      String _plus_7 = (_plus_6 + Integer.valueOf(_points_1));
      String _plus_8 = (_plus_7 + " points");
      Server.log.info(_plus_8);
    };
    _subscribeOn_1.subscribe(_function_1);
    PublishSubject<Pair<PointRequest, JsonDocument>> _pointsAwarded = this.database.getPointsAwarded();
    Scheduler _computation_2 = Schedulers.computation();
    Observable<Pair<PointRequest, JsonDocument>> _subscribeOn_2 = _pointsAwarded.subscribeOn(_computation_2);
    final Action1<Pair<PointRequest, JsonDocument>> _function_2 = (Pair<PointRequest, JsonDocument> it) -> {
      com.esotericsoftware.kryonet.Server _server = this.connector.getServer();
      Connection[] _connections = _server.getConnections();
      final Function1<Connection, Boolean> _function_3 = (Connection connection) -> {
        int _iD = connection.getID();
        PointRequest _key = it.getKey();
        int _clientID = _key.getClientID();
        return Boolean.valueOf((_iD == _clientID));
      };
      final Connection connection = IterableExtensions.<Connection>findFirst(((Iterable<Connection>)Conversions.doWrapArray(_connections)), _function_3);
      JsonDocument _value = it.getValue();
      String _id = _value.id();
      JsonDocument _value_1 = it.getValue();
      JsonObject _content = _value_1.content();
      Integer _int = _content.getInt("points");
      NotifySuccesful _notifySuccesful = new NotifySuccesful(_id, (_int).intValue());
      connection.sendTCP(_notifySuccesful);
      JsonDocument _value_2 = it.getValue();
      JsonObject _content_1 = _value_2.content();
      Integer _int_1 = _content_1.getInt("checkedInCount");
      int _modulo = ((_int_1).intValue() % 6);
      boolean _equals = (_modulo == 0);
      if (_equals) {
        JsonDocument _value_3 = it.getValue();
        String _id_1 = _value_3.id();
        String _plus = (_id_1 + " is now allowed to go to the battleground");
        Server.log.info(_plus);
        JsonDocument _value_4 = it.getValue();
        String _id_2 = _value_4.id();
        NotifyBattleground _notifyBattleground = new NotifyBattleground(_id_2);
        connection.sendTCP(_notifyBattleground);
      }
    };
    _subscribeOn_2.subscribe(_function_2);
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
