package com.sirolf2009.lsw2017.client.net;

import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import com.google.gson.Gson;
import com.kstruct.gethostname4j.Hostname;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sirolf2009.lsw2017.common.Queues;
import com.sirolf2009.lsw2017.common.model.Handshake;
import com.sirolf2009.lsw2017.common.model.NotifySuccesful;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import java.io.Closeable;
import java.io.IOException;
import javafx.application.Platform;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class Connector implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Connection connection;
  
  private final Channel channel;
  
  private final String acceptedQueue;
  
  private final String deniedQueue;
  
  private final String battlegroundQueue;
  
  public Connector() {
    try {
      final ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      factory.setPort(5672);
      this.connection = factory.newConnection();
      this.channel = this.connection.createChannel();
      Queues.declareQueues(this.channel);
      this.acceptedQueue = this.channel.queueDeclare().getQueue();
      this.deniedQueue = this.channel.queueDeclare().getQueue();
      this.battlegroundQueue = this.channel.queueDeclare().getQueue();
      String _hostname = Hostname.getHostname();
      Handshake _handshake = new Handshake(_hostname, this.acceptedQueue, this.deniedQueue, this.battlegroundQueue);
      this.send(Queues.CONNECTED, _handshake);
      this.channel.basicConsume(this.acceptedQueue, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          final NotifySuccesful notify = _gson.<NotifySuccesful>fromJson(_string, NotifySuccesful.class);
          final Runnable _function = () -> {
            final TrayNotification notification = new TrayNotification();
            notification.setTitle("Succes");
            StringConcatenation _builder = new StringConcatenation();
            String _teamName = notify.getTeamName();
            _builder.append(_teamName);
            _builder.append(" has received ");
            int _points = notify.getPoints();
            _builder.append(_points);
            _builder.append(" points");
            notification.setMessage(_builder.toString());
            notification.setNotification(Notifications.SUCCESS);
            notification.showAndDismiss(Duration.seconds(1));
          };
          Platform.runLater(_function);
        }
      });
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void requestPoints(final PointRequest request) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("requesting ");
    _builder.append(request);
    Connector.log.info(_builder);
    this.send(Queues.POINT_REQUEST, request);
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
  
  @Override
  public void close() throws IOException {
    try {
      this.channel.close();
      this.connection.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
