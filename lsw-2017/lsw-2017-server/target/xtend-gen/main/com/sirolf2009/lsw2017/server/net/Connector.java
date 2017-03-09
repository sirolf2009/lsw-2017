package com.sirolf2009.lsw2017.server.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sirolf2009.lsw2017.common.Network;
import com.sirolf2009.lsw2017.server.net.Facade;
import java.net.InetSocketAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class Connector {
  private final static Logger log = LogManager.getLogger();
  
  private final Facade facade;
  
  private final Server server;
  
  public Connector() {
    try {
      Facade _facade = new Facade();
      this.facade = _facade;
      this.server = new Server() {
        @Override
        protected Connection newConnection() {
          return Connector.this.facade;
        }
      };
      Network.register(this.server);
      this.server.start();
      this.server.addListener(new Listener() {
        @Override
        public void connected(final Connection connection) {
          InetSocketAddress _remoteAddressTCP = connection.getRemoteAddressTCP();
          String _plus = (_remoteAddressTCP + "");
          connection.setName(_plus);
          String _plus_1 = (connection + " connected");
          Connector.log.info(_plus_1);
        }
        
        @Override
        public void disconnected(final Connection connection) {
          String _plus = (connection + " disconnected");
          Connector.log.info(_plus);
        }
      });
      this.server.bind(1234);
      Connector.log.info("Connector initialized");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Pure
  public Facade getFacade() {
    return this.facade;
  }
  
  @Pure
  public Server getServer() {
    return this.server;
  }
}
