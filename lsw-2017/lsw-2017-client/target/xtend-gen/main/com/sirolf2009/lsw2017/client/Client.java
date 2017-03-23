package com.sirolf2009.lsw2017.client;

import com.google.common.base.Objects;
import com.sirolf2009.lsw2017.client.net.Connector;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    final Connector connector = new Connector();
    StackPane _stackPane = new StackPane();
    final Procedure1<StackPane> _function = (StackPane it) -> {
      ObservableList<String> _stylesheets = it.getStylesheets();
      _stylesheets.add("client.css");
      ObservableList<Node> _children = it.getChildren();
      StackPane _stackPane_1 = new StackPane();
      final Procedure1<StackPane> _function_1 = (StackPane it_1) -> {
        it_1.setAlignment(Pos.TOP_RIGHT);
        ObservableList<Node> _children_1 = it_1.getChildren();
        Label _label = new Label("");
        final Procedure1<Label> _function_2 = (Label it_2) -> {
          final Procedure1<Object> _function_3 = (Object listener) -> {
            boolean _get = connector.getConnected().get();
            if (_get) {
              Image _image = new Image("green_dot.png");
              ImageView _imageView = new ImageView(_image);
              it_2.setGraphic(_imageView);
              it_2.setText("connected");
            } else {
              Image _image_1 = new Image("red_dot.png");
              ImageView _imageView_1 = new ImageView(_image_1);
              it_2.setGraphic(_imageView_1);
              it_2.setText("disconnected");
            }
          };
          final Procedure1<Object> setConnection = _function_3;
          setConnection.apply(null);
          final InvalidationListener _function_4 = (Observable listener) -> {
            setConnection.apply(listener);
          };
          connector.getConnected().addListener(_function_4);
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
          int _iD = connector.getClient().getID();
          String _text = team.getText();
          int _parseInt = Integer.parseInt(points.getText());
          long _currentTimeMillis = System.currentTimeMillis();
          PointRequest _pointRequest = new PointRequest(_iD, _text, _parseInt, _currentTimeMillis);
          connector.getProxy().requestPoints(_pointRequest);
          team.clear();
          points.clear();
          team.requestFocus();
        } catch (final Throwable _t) {
          if (_t instanceof Exception) {
            final Exception e = (Exception)_t;
            final Runnable _function_5 = () -> {
              Notification.Notifier.INSTANCE.notifyError("Failure!", "Uh oh, something went wrong");
            };
            Platform.runLater(_function_5);
            this.log.error("Failed to send point request to the server. Team={} Points={} Time={}", team.getText(), points.getText(), Long.valueOf(System.currentTimeMillis()), e);
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
      ObservableList<Node> _children_2 = it.getChildren();
      StackPane _stackPane_2 = new StackPane();
      final Procedure1<StackPane> _function_8 = (StackPane it_1) -> {
        it_1.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> _children_3 = it_1.getChildren();
        ToggleButton _toggleButton = new ToggleButton("");
        final Procedure1<ToggleButton> _function_9 = (ToggleButton it_2) -> {
          final EventHandler<ActionEvent> _function_10 = (ActionEvent event) -> {
            boolean _isFullScreen = stage.isFullScreen();
            boolean _not = (!_isFullScreen);
            stage.setFullScreen(_not);
          };
          it_2.setOnAction(_function_10);
          ObservableList<String> _styleClass = it_2.getStyleClass();
          _styleClass.add("fullscreen-button");
          it_2.setMinHeight(32);
          it_2.setMinWidth(32);
        };
        ToggleButton _doubleArrow_2 = ObjectExtensions.<ToggleButton>operator_doubleArrow(_toggleButton, _function_9);
        _children_3.add(_doubleArrow_2);
        it_1.setPickOnBounds(false);
      };
      StackPane _doubleArrow_2 = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane_2, _function_8);
      _children_2.add(_doubleArrow_2);
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
