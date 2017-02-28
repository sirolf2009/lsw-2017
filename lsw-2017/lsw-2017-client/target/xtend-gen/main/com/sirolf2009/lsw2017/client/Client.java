package com.sirolf2009.lsw2017.client;

import com.sirolf2009.lsw2017.client.net.Connector;
import com.sirolf2009.lsw2017.common.ServerProxy;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import xtendfx.FXApp;

@FXApp
@SuppressWarnings("all")
public class Client extends Application {
  @Override
  public void start(final Stage it) throws Exception {
    it.setTitle("LSW 2017");
    Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
    final Connector connector = new Connector();
    StackPane _stackPane = new StackPane();
    final Procedure1<StackPane> _function = (StackPane it_1) -> {
      ObservableList<Node> _children = it_1.getChildren();
      StackPane _stackPane_1 = new StackPane();
      final Procedure1<StackPane> _function_1 = (StackPane it_2) -> {
        it_2.setAlignment(Pos.TOP_RIGHT);
        ObservableList<Node> _children_1 = it_2.getChildren();
        Label _label = new Label("");
        final Procedure1<Label> _function_2 = (Label it_3) -> {
          SimpleBooleanProperty _connected = connector.getConnected();
          boolean _get = _connected.get();
          if (_get) {
            Image _image = new Image("green_dot.png");
            ImageView _imageView = new ImageView(_image);
            it_3.setGraphic(_imageView);
            it_3.setText("connected");
          } else {
            Image _image_1 = new Image("red_dot.png");
            ImageView _imageView_1 = new ImageView(_image_1);
            it_3.setGraphic(_imageView_1);
            it_3.setText("disconnected");
          }
          SimpleBooleanProperty _connected_1 = connector.getConnected();
          final InvalidationListener _function_3 = (Observable listener) -> {
            SimpleBooleanProperty _connected_2 = connector.getConnected();
            boolean _get_1 = _connected_2.get();
            if (_get_1) {
              Image _image_2 = new Image("green_dot.png");
              ImageView _imageView_2 = new ImageView(_image_2);
              it_3.setGraphic(_imageView_2);
              it_3.setText("connected");
            } else {
              Image _image_3 = new Image("red_dot.png");
              ImageView _imageView_3 = new ImageView(_image_3);
              it_3.setGraphic(_imageView_3);
              it_3.setText("disconnected");
            }
          };
          _connected_1.addListener(_function_3);
        };
        Label _doubleArrow = ObjectExtensions.<Label>operator_doubleArrow(_label, _function_2);
        _children_1.add(_doubleArrow);
      };
      StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane_1, _function_1);
      _children.add(_doubleArrow);
      TextField _textField = new TextField();
      final Procedure1<TextField> _function_2 = (TextField it_2) -> {
        it_2.setMaxWidth(400);
        it_2.requestFocus();
      };
      final TextField team = ObjectExtensions.<TextField>operator_doubleArrow(_textField, _function_2);
      TextField _textField_1 = new TextField();
      final Procedure1<TextField> _function_3 = (TextField it_2) -> {
        it_2.setMaxWidth(400);
      };
      final TextField points = ObjectExtensions.<TextField>operator_doubleArrow(_textField_1, _function_3);
      ObservableList<Node> _children_1 = it_1.getChildren();
      VBox _vBox = new VBox();
      final Procedure1<VBox> _function_4 = (VBox it_2) -> {
        it_2.setAlignment(Pos.CENTER);
        ObservableList<Node> _children_2 = it_2.getChildren();
        HBox _hBox = new HBox();
        final Procedure1<HBox> _function_5 = (HBox it_3) -> {
          it_3.setAlignment(Pos.CENTER);
          ObservableList<Node> _children_3 = it_3.getChildren();
          Text _text = new Text("Team");
          _children_3.add(_text);
          ObservableList<Node> _children_4 = it_3.getChildren();
          _children_4.add(team);
        };
        HBox _doubleArrow_1 = ObjectExtensions.<HBox>operator_doubleArrow(_hBox, _function_5);
        _children_2.add(_doubleArrow_1);
        ObservableList<Node> _children_3 = it_2.getChildren();
        HBox _hBox_1 = new HBox();
        final Procedure1<HBox> _function_6 = (HBox it_3) -> {
          it_3.setAlignment(Pos.CENTER);
          ObservableList<Node> _children_4 = it_3.getChildren();
          Text _text = new Text("Points");
          _children_4.add(_text);
          ObservableList<Node> _children_5 = it_3.getChildren();
          _children_5.add(points);
        };
        HBox _doubleArrow_2 = ObjectExtensions.<HBox>operator_doubleArrow(_hBox_1, _function_6);
        _children_3.add(_doubleArrow_2);
        ObservableList<Node> _children_4 = it_2.getChildren();
        Button _button = new Button();
        final Procedure1<Button> _function_7 = (Button it_3) -> {
          it_3.setText("Submit");
          final EventHandler<ActionEvent> _function_8 = (ActionEvent it_4) -> {
            ServerProxy _proxy = connector.getProxy();
            PointRequest _pointRequest = new PointRequest();
            final Procedure1<PointRequest> _function_9 = (PointRequest it_5) -> {
              String _text = team.getText();
              it_5.setTeamName(_text);
              String _text_1 = points.getText();
              int _parseInt = Integer.parseInt(_text_1);
              it_5.setPoints(_parseInt);
            };
            PointRequest _doubleArrow_3 = ObjectExtensions.<PointRequest>operator_doubleArrow(_pointRequest, _function_9);
            _proxy.requestPoints(_doubleArrow_3);
            team.clear();
            points.clear();
          };
          it_3.setOnAction(_function_8);
        };
        Button _doubleArrow_3 = ObjectExtensions.<Button>operator_doubleArrow(_button, _function_7);
        _children_4.add(_doubleArrow_3);
      };
      VBox _doubleArrow_1 = ObjectExtensions.<VBox>operator_doubleArrow(_vBox, _function_4);
      _children_1.add(_doubleArrow_1);
    };
    StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane, _function);
    Scene _scene = new Scene(_doubleArrow, 800, 600);
    it.setScene(_scene);
    it.show();
  }
  
  public static void main(final String[] args) {
    Application.launch(args);
  }
}
