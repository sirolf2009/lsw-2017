package com.sirolf2009.lsw2017.client.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import com.sirolf2009.lsw2017.common.Network;
import com.sirolf2009.lsw2017.common.ServerProxy;
import com.sirolf2009.lsw2017.common.model.NotifyBattleground;
import com.sirolf2009.lsw2017.common.model.NotifySuccesful;
import com.sirolf2009.lsw2017.common.model.NotifyWait;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Connector {
  private final static Logger log = LogManager.getLogger();
  
  @Accessors
  private final Client client;
  
  @Accessors
  private ServerProxy proxy;
  
  @Accessors
  private final SimpleBooleanProperty connected;
  
  public Connector() {
    SimpleBooleanProperty _simpleBooleanProperty = new SimpleBooleanProperty(false);
    this.connected = _simpleBooleanProperty;
    Client _client = new Client();
    this.client = _client;
    this.client.start();
    Network.register(this.client);
    this.client.addListener(new Listener() {
      @Override
      public void connected(final Connection arg0) {
        Connector.log.info("connected");
        final Runnable _function = () -> {
          Connector.this.connected.set(true);
        };
        Platform.runLater(_function);
      }
      
      @Override
      public void disconnected(final Connection arg0) {
        Connector.log.warn("disconnected");
        final Runnable _function = () -> {
          Connector.this.connected.set(false);
        };
        Platform.runLater(_function);
        Connector.this.connect();
      }
      
      @Override
      public void received(final Connection connection, final Object packet) {
        if ((packet instanceof NotifySuccesful)) {
          final NotifySuccesful succes = ((NotifySuccesful) packet);
          final Runnable _function = () -> {
            final TrayNotification notification = new TrayNotification();
            notification.setTitle("Success");
            String _teamName = succes.getTeamName();
            String _plus = (_teamName + " now has ");
            int _points = succes.getPoints();
            String _plus_1 = (_plus + Integer.valueOf(_points));
            String _plus_2 = (_plus_1 + " points");
            notification.setMessage(_plus_2);
            notification.setNotification(Notifications.SUCCESS);
            notification.showAndDismiss(Duration.seconds(1));
          };
          Platform.runLater(_function);
        } else {
          if ((packet instanceof NotifyBattleground)) {
            final NotifyBattleground battleground = ((NotifyBattleground) packet);
            final Runnable _function_1 = () -> {
              final TrayNotification notification = new TrayNotification();
              notification.setTitle("Battleground");
              String _teamName = battleground.getTeamName();
              String _plus = (_teamName + " must now go to the battleground");
              notification.setMessage(_plus);
              notification.setNotification(Notifications.NOTICE);
              notification.showAndDismiss(Duration.seconds(1));
            };
            Platform.runLater(_function_1);
          } else {
            if ((packet instanceof NotifyWait)) {
              final NotifyWait wait = ((NotifyWait) packet);
              final Runnable _function_2 = () -> {
                final TrayNotification notification = new TrayNotification();
                notification.setTitle("Denied!");
                String _teamName = wait.getTeamName();
                String _plus = (_teamName + " must wait a little while longer before scanning");
                notification.setMessage(_plus);
                notification.setNotification(Notifications.ERROR);
                notification.showAndDismiss(Duration.seconds(1));
              };
              Platform.runLater(_function_2);
            }
          }
        }
      }
    });
    this.connect();
  }
  
  public void connect() {
    final Runnable _function = () -> {
      try {
        while ((!this.client.isConnected())) {
          try {
            this.client.connect(5000, "localhost", 1234);
            this.proxy = ObjectSpace.<ServerProxy>getRemoteObject(this.client, 1, ServerProxy.class);
          } catch (final Throwable _t) {
            if (_t instanceof Exception) {
              final Exception e = (Exception)_t;
              Connector.log.error("Failed to connect", e);
              Thread.sleep(1000);
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    new Thread(_function).start();
  }
  
  @Pure
  public Client getClient() {
    return this.client;
  }
  
  @Pure
  public ServerProxy getProxy() {
    return this.proxy;
  }
  
  public void setProxy(final ServerProxy proxy) {
    this.proxy = proxy;
  }
  
  @Pure
  public SimpleBooleanProperty getConnected() {
    return this.connected;
  }
}
