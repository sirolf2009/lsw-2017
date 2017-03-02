package com.sirolf2009.lsw2017.server;

import com.sirolf2009.lsw2017.common.model.PointRequest;
import com.sirolf2009.lsw2017.server.net.Connector;
import com.sirolf2009.lsw2017.server.net.Database;
import com.sirolf2009.lsw2017.server.net.Facade;
import java.io.Closeable;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
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
      String _teamName = it.getTeamName();
      boolean _doesTeamExist = this.database.doesTeamExist(_teamName);
      boolean _not = (!_doesTeamExist);
      if (_not) {
        String _teamName_1 = it.getTeamName();
        this.database.createNewTeam(_teamName_1);
      }
      String _teamName_2 = it.getTeamName();
      int _points = it.getPoints();
      long _currentTime = it.getCurrentTime();
      this.database.awardPoints(_teamName_2, _points, _currentTime);
      String _teamName_3 = it.getTeamName();
      String _plus = ("Awarded " + _teamName_3);
      String _plus_1 = (_plus + " ");
      int _points_1 = it.getPoints();
      String _plus_2 = (_plus_1 + Integer.valueOf(_points_1));
      String _plus_3 = (_plus_2 + " points");
      Server.log.info(_plus_3);
    };
    _subscribeOn.subscribe(_function);
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
