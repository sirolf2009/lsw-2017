package com.sirolf2009.lsw2017.server.net;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sirolf2009.lsw2017.common.Queues;
import com.sirolf2009.lsw2017.common.model.Handshake;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class Connector {
  private final static Logger log = LogManager.getLogger();
  
  private final Channel channel;
  
  private final Subject<Handshake> connected;
  
  private final Subject<PointRequest> pointRequest;
  
  public Connector() {
    try {
      final ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      factory.setPort(5672);
      final Connection connection = factory.newConnection();
      this.channel = connection.createChannel();
      Queues.declareQueues(this.channel);
      this.connected = ReplaySubject.<Handshake>create();
      this.channel.basicConsume(Queues.CONNECTED, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          Connector.this.connected.onNext(_gson.<Handshake>fromJson(_string, Handshake.class));
        }
      });
      this.pointRequest = ReplaySubject.<PointRequest>create();
      this.channel.basicConsume(Queues.POINT_REQUEST, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          Connector.this.pointRequest.onNext(_gson.<PointRequest>fromJson(_string, PointRequest.class));
        }
      });
      Connector.log.info("Connector initialized");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void send(final String channelName, final Object object) {
    try {
      this.channel.basicPublish("", channelName, null, new Gson().toJson(object).getBytes());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void send(final String channelName, final String message) {
    try {
      this.channel.basicPublish("", channelName, null, message.getBytes());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Pure
  public Channel getChannel() {
    return this.channel;
  }
  
  @Pure
  public Subject<Handshake> getConnected() {
    return this.connected;
  }
  
  @Pure
  public Subject<PointRequest> getPointRequest() {
    return this.pointRequest;
  }
}
