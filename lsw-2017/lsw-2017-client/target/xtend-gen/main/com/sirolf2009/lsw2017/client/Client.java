package com.sirolf2009.lsw2017.client;

import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import com.google.common.base.Objects;
import com.kstruct.gethostname4j.Hostname;
import com.sirolf2009.lsw2017.client.net.Connector;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import xtendfx.FXApp;

@FXApp
@SuppressWarnings("all")
public class Client extends Application {
  private final Logger log = LogManager.getLogger();
  
  @Override
  public void start(final Stage stage) throws Exception {
    stage.setTitle("LSW 2017");
    Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
    final Connector connector = new Connector(stage);
    StackPane _stackPane = new StackPane();
    final Procedure1<StackPane> _function = (StackPane it) -> {
      ObservableList<String> _stylesheets = it.getStylesheets();
      _stylesheets.add("client.css");
      ObservableList<Node> _children = it.getChildren();
      StackPane _stackPane_1 = new StackPane();
      final Procedure1<StackPane> _function_1 = (StackPane it_1) -> {
        it_1.setAlignment(Pos.TOP_CENTER);
        ObservableList<Node> _children_1 = it_1.getChildren();
        Label _label = new Label("SuperShurkenBoek");
        final Procedure1<Label> _function_2 = (Label it_2) -> {
          ObservableList<String> _styleClass = it_2.getStyleClass();
          _styleClass.add("title");
        };
        Label _doubleArrow = ObjectExtensions.<Label>operator_doubleArrow(_label, _function_2);
        _children_1.add(_doubleArrow);
      };
      StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane_1, _function_1);
      _children.add(_doubleArrow);
      TextField _textField = new TextField();
      final Procedure1<TextField> _function_2 = (TextField it_1) -> {
        it_1.setMaxWidth(400);
        it_1.requestFocus();
      };
      final TextField team = ObjectExtensions.<TextField>operator_doubleArrow(_textField, _function_2);
      TextField _textField_1 = new TextField();
      final Procedure1<TextField> _function_3 = (TextField it_1) -> {
        it_1.setMaxWidth(400);
      };
      final TextField points = ObjectExtensions.<TextField>operator_doubleArrow(_textField_1, _function_3);
      final Procedure1<Event> _function_4 = (Event it_1) -> {
        try {
          String _hostname = Hostname.getHostname();
          String _text = team.getText();
          String _text_1 = points.getText();
          long _currentTimeMillis = System.currentTimeMillis();
          PointRequest _pointRequest = new PointRequest(_hostname, _text, _text_1, _currentTimeMillis);
          connector.requestPoints(_pointRequest);
          team.clear();
          points.clear();
          team.requestFocus();
        } catch (final Throwable _t) {
          if (_t instanceof Exception) {
            final Exception e = (Exception)_t;
            final Runnable _function_5 = () -> {
              final TrayNotification notification = new TrayNotification();
              notification.setTitle("Failure!");
              notification.setMessage("Uh oh, something went wrong");
              notification.setNotification(Notifications.ERROR);
              notification.showAndDismiss(Duration.seconds(1));
            };
            Platform.runLater(_function_5);
            this.log.error("Failed to send point request to the server. Team={} Points={} Time={}", team.getText(), 
              points.getText(), Long.valueOf(System.currentTimeMillis()), e);
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      };
      final Procedure1<Event> sendPointsToServer = _function_4;
      final EventHandler<KeyEvent> _function_5 = (KeyEvent it_1) -> {
        KeyCode _code = it_1.getCode();
        boolean _equals = Objects.equal(_code, KeyCode.ENTER);
        if (_equals) {
          points.requestFocus();
        }
      };
      team.<KeyEvent>addEventFilter(KeyEvent.KEY_PRESSED, _function_5);
      final EventHandler<KeyEvent> _function_6 = (KeyEvent it_1) -> {
        KeyCode _code = it_1.getCode();
        boolean _equals = Objects.equal(_code, KeyCode.ENTER);
        if (_equals) {
          sendPointsToServer.apply(it_1);
        }
      };
      points.<KeyEvent>addEventFilter(KeyEvent.KEY_PRESSED, _function_6);
      ObservableList<Node> _children_1 = it.getChildren();
      VBox _vBox = new VBox();
      final Procedure1<VBox> _function_7 = (VBox it_1) -> {
        it_1.setAlignment(Pos.CENTER);
        ObservableList<Node> _children_2 = it_1.getChildren();
        HBox _hBox = new HBox();
        final Procedure1<HBox> _function_8 = (HBox it_2) -> {
          it_2.setAlignment(Pos.CENTER);
          ObservableList<Node> _children_3 = it_2.getChildren();
          Text _text = new Text("Team");
          _children_3.add(_text);
          ObservableList<Node> _children_4 = it_2.getChildren();
          _children_4.add(team);
        };
        HBox _doubleArrow_1 = ObjectExtensions.<HBox>operator_doubleArrow(_hBox, _function_8);
        _children_2.add(_doubleArrow_1);
        ObservableList<Node> _children_3 = it_1.getChildren();
        HBox _hBox_1 = new HBox();
        final Procedure1<HBox> _function_9 = (HBox it_2) -> {
          it_2.setAlignment(Pos.CENTER);
          ObservableList<Node> _children_4 = it_2.getChildren();
          Text _text = new Text("Points");
          _children_4.add(_text);
          ObservableList<Node> _children_5 = it_2.getChildren();
          _children_5.add(points);
        };
        HBox _doubleArrow_2 = ObjectExtensions.<HBox>operator_doubleArrow(_hBox_1, _function_9);
        _children_3.add(_doubleArrow_2);
        ObservableList<Node> _children_4 = it_1.getChildren();
        Button _button = new Button();
        final Procedure1<Button> _function_10 = (Button it_2) -> {
          it_2.setText("Submit");
          final EventHandler<ActionEvent> _function_11 = (ActionEvent it_3) -> {
            sendPointsToServer.apply(it_3);
          };
          it_2.setOnAction(_function_11);
        };
        Button _doubleArrow_3 = ObjectExtensions.<Button>operator_doubleArrow(_button, _function_10);
        _children_4.add(_doubleArrow_3);
      };
      VBox _doubleArrow_1 = ObjectExtensions.<VBox>operator_doubleArrow(_vBox, _function_7);
      _children_1.add(_doubleArrow_1);
    };
    StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane, _function);
    Scene _scene = new Scene(_doubleArrow, 800, 600);
    stage.setScene(_scene);
    stage.show();
  }
  
  public static void main(final String[] args) {
    Application.launch(args);
  }
}
