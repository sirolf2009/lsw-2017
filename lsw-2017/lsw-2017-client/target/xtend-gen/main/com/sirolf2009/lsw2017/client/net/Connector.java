package com.sirolf2009.lsw2017.client.net;

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
import com.sirolf2009.lsw2017.common.model.NotifyBattleground;
import com.sirolf2009.lsw2017.common.model.NotifySuccesful;
import com.sirolf2009.lsw2017.common.model.NotifyWait;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import java.io.Closeable;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class Connector implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final Stage stage;
  
  private final Connection connection;
  
  private final Channel channel;
  
  private final String acceptedQueue;
  
  private final String deniedQueue;
  
  private final String battlegroundQueue;
  
  public Connector(final Stage stage) {
    try {
      this.stage = stage;
      final ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      factory.setPort(5672);
      this.connection = factory.newConnection();
      this.channel = this.connection.createChannel();
      Queues.declareQueues(this.channel);
      this.acceptedQueue = this.channel.queueDeclare().getQueue();
      this.deniedQueue = this.channel.queueDeclare().getQueue();
      this.battlegroundQueue = this.channel.queueDeclare().getQueue();
      final Runnable _function = () -> {
        while (true) {
          try {
            this.handshake();
            Thread.sleep((10 * 1000));
          } catch (final Throwable _t) {
            if (_t instanceof Exception) {
              final Exception e = (Exception)_t;
              Connector.log.error("Failed to handshake");
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        }
      };
      final Thread heartbeat = new Thread(_function);
      heartbeat.setDaemon(true);
      heartbeat.start();
      this.channel.basicConsume(this.acceptedQueue, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          final NotifySuccesful notify = _gson.<NotifySuccesful>fromJson(_string, NotifySuccesful.class);
          StringConcatenation _builder = new StringConcatenation();
          String _teamName = notify.getTeamName();
          _builder.append(_teamName);
          _builder.append(" heeft zijn punten ontvangen!");
          Connector.this.dialogTimer(_builder.toString(), Duration.seconds(2));
        }
      });
      this.channel.basicConsume(this.deniedQueue, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          final NotifyWait notify = _gson.<NotifyWait>fromJson(_string, NotifyWait.class);
          StringConcatenation _builder = new StringConcatenation();
          String _teamName = notify.getTeamName();
          _builder.append(_teamName);
          _builder.append(" moet nog iets langer wachten!");
          Connector.this.dialogButton(_builder.toString());
        }
      });
      this.channel.basicConsume(this.battlegroundQueue, true, new DefaultConsumer(this.channel) {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
          Gson _gson = new Gson();
          String _string = new String(body);
          final NotifyBattleground notify = _gson.<NotifyBattleground>fromJson(_string, NotifyBattleground.class);
          StringConcatenation _builder = new StringConcatenation();
          String _teamName = notify.getTeamName();
          _builder.append(_teamName);
          _builder.append(" moet nu naar slachtveld ");
          int _battleground = notify.getBattleground();
          _builder.append(_battleground);
          Connector.this.dialogButton(_builder.toString());
        }
      });
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void handshake() {
    String _hostname = Hostname.getHostname();
    Handshake _handshake = new Handshake(_hostname, this.acceptedQueue, this.deniedQueue, this.battlegroundQueue);
    this.send(Queues.CONNECTED, _handshake);
  }
  
  public void dialogTimer(final String text, final Duration duration) {
    final Procedure1<Pair<VBox, Stage>> _function = (Pair<VBox, Stage> it) -> {
      final Stage stage = it.getValue();
      final EventHandler<ActionEvent> _function_1 = (ActionEvent it_1) -> {
        stage.hide();
      };
      KeyFrame _keyFrame = new KeyFrame(duration, _function_1);
      final Timeline timeline = new Timeline(_keyFrame);
      timeline.play();
    };
    this.dialog(text, _function);
  }
  
  public void dialogButton(final String text) {
    final Procedure1<Pair<VBox, Stage>> _function = (Pair<VBox, Stage> it) -> {
      final Stage stage = it.getValue();
      final VBox it_1 = it.getKey();
      ObservableList<Node> _children = it_1.getChildren();
      Button _button = new Button("OK");
      final Procedure1<Button> _function_1 = (Button it_2) -> {
        final EventHandler<ActionEvent> _function_2 = (ActionEvent it_3) -> {
          stage.hide();
        };
        it_2.setOnAction(_function_2);
      };
      Button _doubleArrow = ObjectExtensions.<Button>operator_doubleArrow(_button, _function_1);
      _children.add(_doubleArrow);
    };
    this.dialog(text, _function);
  }
  
  public void dialog(final String text, final Procedure1<? super Pair<VBox, Stage>> controls) {
    final Runnable _function = () -> {
      final Stage dialog = new Stage();
      dialog.initStyle(StageStyle.UNDECORATED);
      dialog.initModality(Modality.APPLICATION_MODAL);
      dialog.initOwner(this.stage);
      StackPane _stackPane = new StackPane();
      final Procedure1<StackPane> _function_1 = (StackPane it) -> {
        it.setAlignment(Pos.CENTER);
        ObservableList<Node> _children = it.getChildren();
        VBox _vBox = new VBox();
        final Procedure1<VBox> _function_2 = (VBox it_1) -> {
          it_1.setAlignment(Pos.CENTER);
          ObservableList<Node> _children_1 = it_1.getChildren();
          Text _text = new Text(text);
          _children_1.add(_text);
          Pair<VBox, Stage> _mappedTo = Pair.<VBox, Stage>of(it_1, dialog);
          controls.apply(_mappedTo);
        };
        VBox _doubleArrow = ObjectExtensions.<VBox>operator_doubleArrow(_vBox, _function_2);
        _children.add(_doubleArrow);
      };
      final StackPane container = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane, _function_1);
      final Scene dialogScene = new Scene(container, 300, 200);
      dialog.setScene(dialogScene);
      dialog.show();
      dialog.toFront();
    };
    Platform.runLater(_function);
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
