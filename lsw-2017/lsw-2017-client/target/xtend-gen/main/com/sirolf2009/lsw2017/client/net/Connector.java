package com.sirolf2009.lsw2017.client.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.sirolf2009.lsw2017.common.Network;
import com.sirolf2009.lsw2017.common.ServerProxy;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Connector {
  private final static Logger log = LogManager.getLogger();
  
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
    });
    this.connect();
  }
  
  public void connect() {
    final Runnable _function = () -> {
      try {
        while ((!this.client.isConnected())) {
          try {
            this.client.connect(5000, "localhost", 1234);
            ServerProxy _remoteObject = ObjectSpace.<ServerProxy>getRemoteObject(this.client, 1, ServerProxy.class);
            this.proxy = _remoteObject;
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
    Thread _thread = new Thread(_function);
    _thread.start();
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
