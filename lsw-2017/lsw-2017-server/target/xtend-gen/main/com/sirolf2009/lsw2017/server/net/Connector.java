package com.sirolf2009.lsw2017.server.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sirolf2009.lsw2017.common.Network;
import com.sirolf2009.lsw2017.server.net.Facade;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class Connector {
  private final static Logger log = LogManager.getLogger();
  
  private final Facade facade;
  
  private final Server server;
  
  public Connector() {
    try {
      final Properties configProperties = new Properties();
      configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "digital:2181");
      configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "connected-consumer");
      configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
      InputOutput.<String>println("listening");
      final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configProperties);
      consumer.subscribe(Arrays.<String>asList("connected"));
      InputOutput.<String>println("listening");
      final ConsumerRecords<String, String> records = consumer.poll(10000);
      InputOutput.<String>println("received");
      final Consumer<ConsumerRecord<String, String>> _function = (ConsumerRecord<String, String> it) -> {
        InputOutput.<ConsumerRecord<String, String>>println(it);
      };
      records.forEach(_function);
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
