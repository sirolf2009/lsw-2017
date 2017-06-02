package com.sirolf2009.lsw2017.leaderboard;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.sirolf2009.lsw2017.leaderboard.Names;
import com.sirolf2009.lsw2017.leaderboard.model.Team;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import xtendfx.FXApp;

@FXApp
@SuppressWarnings("all")
public class Leaderboard extends Application {
  private final ObservableList<Team> teams = FXCollections.<Team>observableArrayList();
  
  private final Date endDate = new Function0<Date>() {
    public Date apply() {
      Date _xblockexpression = null;
      {
        final Calendar cal = Calendar.getInstance();
        cal.set(2017, Calendar.JUNE, 3, 17, 0, 0);
        _xblockexpression = cal.getTime();
      }
      return _xblockexpression;
    }
  }.apply();
  
  @Override
  public void start(final Stage it) {
    try {
      it.setTitle("LSW 2017 leaderboard");
      final Cluster cluster = Cluster.builder().addContactPoint("localhost").withPort(32769).build();
      final Session session = cluster.connect("lsw2017");
      final Runnable _function = () -> {
        while (true) {
          try {
            final Function1<Row, Team> _function_1 = (Row it_1) -> {
              String _villainName = Names.getVillainName(it_1.getString("teamname"));
              String _string = it_1.getString("teamname");
              int _int = it_1.getInt("points");
              String _subkamp = Names.getSubkamp(it_1.getString("teamname"));
              long _time = it_1.<Date>get("lastcheckedin", Date.class).getTime();
              return new Team(_villainName, _string, _int, _subkamp, _time);
            };
            final Consumer<Team> _function_2 = (Team it_1) -> {
              final Function1<Team, Boolean> _function_3 = (Team data) -> {
                return Boolean.valueOf(data.getName().equals(it_1.getName()));
              };
              final Team existing = IterableExtensions.<Team>findFirst(this.teams, _function_3);
              if ((existing == null)) {
                this.teams.add(it_1);
              } else {
                existing.setLikes(it_1.getLikes());
              }
            };
            ListExtensions.<Row, Team>map(session.execute("SELECT * FROM teams").all(), _function_1).forEach(_function_2);
            Thread.sleep(1000);
          } catch (final Throwable _t) {
            if (_t instanceof Exception) {
              final Exception e = (Exception)_t;
              e.printStackTrace();
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        }
      };
      new Thread(_function).start();
      StackPane _stackPane = new StackPane();
      final Procedure1<StackPane> _function_1 = (StackPane it_1) -> {
        GridPane _gridPane = new GridPane();
        final Procedure1<GridPane> _function_2 = (GridPane it_2) -> {
          ObservableList<ColumnConstraints> _columnConstraints = it_2.getColumnConstraints();
          ColumnConstraints _columnConstraints_1 = new ColumnConstraints();
          final Procedure1<ColumnConstraints> _function_3 = (ColumnConstraints it_3) -> {
            it_3.setPercentWidth(10);
          };
          ColumnConstraints _doubleArrow = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_1, _function_3);
          _columnConstraints.add(_doubleArrow);
          ObservableList<ColumnConstraints> _columnConstraints_2 = it_2.getColumnConstraints();
          ColumnConstraints _columnConstraints_3 = new ColumnConstraints();
          final Procedure1<ColumnConstraints> _function_4 = (ColumnConstraints it_3) -> {
            it_3.setPercentWidth(80);
          };
          ColumnConstraints _doubleArrow_1 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_3, _function_4);
          _columnConstraints_2.add(_doubleArrow_1);
          ObservableList<ColumnConstraints> _columnConstraints_4 = it_2.getColumnConstraints();
          ColumnConstraints _columnConstraints_5 = new ColumnConstraints();
          final Procedure1<ColumnConstraints> _function_5 = (ColumnConstraints it_3) -> {
            it_3.setPercentWidth(10);
          };
          ColumnConstraints _doubleArrow_2 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_5, _function_5);
          _columnConstraints_4.add(_doubleArrow_2);
          ObservableList<RowConstraints> _rowConstraints = it_2.getRowConstraints();
          RowConstraints _rowConstraints_1 = new RowConstraints();
          final Procedure1<RowConstraints> _function_6 = (RowConstraints it_3) -> {
            it_3.setPercentHeight(10);
          };
          RowConstraints _doubleArrow_3 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_1, _function_6);
          _rowConstraints.add(_doubleArrow_3);
          ObservableList<RowConstraints> _rowConstraints_2 = it_2.getRowConstraints();
          RowConstraints _rowConstraints_3 = new RowConstraints();
          final Procedure1<RowConstraints> _function_7 = (RowConstraints it_3) -> {
            it_3.setPercentHeight(80);
          };
          RowConstraints _doubleArrow_4 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_3, _function_7);
          _rowConstraints_2.add(_doubleArrow_4);
          ObservableList<RowConstraints> _rowConstraints_4 = it_2.getRowConstraints();
          RowConstraints _rowConstraints_5 = new RowConstraints();
          final Procedure1<RowConstraints> _function_8 = (RowConstraints it_3) -> {
            it_3.setPercentHeight(10);
          };
          RowConstraints _doubleArrow_5 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_5, _function_8);
          _rowConstraints_4.add(_doubleArrow_5);
          TableView<Team> _tableView = new TableView<Team>();
          final Procedure1<TableView<Team>> _function_9 = (TableView<Team> it_3) -> {
            final TableColumn<Team, String> teamCol = new TableColumn<Team, String>("Team");
            final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_10 = (TableColumn.CellDataFeatures<Team, String> it_4) -> {
              return it_4.getValue().teamProperty();
            };
            teamCol.setCellValueFactory(_function_10);
            final TableColumn<Team, String> nameCol = new TableColumn<Team, String>("Naam");
            final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_11 = (TableColumn.CellDataFeatures<Team, String> it_4) -> {
              return it_4.getValue().nameProperty();
            };
            nameCol.setCellValueFactory(_function_11);
            final TableColumn<Team, Number> likesCol = new TableColumn<Team, Number>("Vind ik leuks");
            final Callback<TableColumn.CellDataFeatures<Team, Number>, ObservableValue<Number>> _function_12 = (TableColumn.CellDataFeatures<Team, Number> it_4) -> {
              return it_4.getValue().likesProperty();
            };
            likesCol.setCellValueFactory(_function_12);
            final TableColumn<Team, String> subcampCol = new TableColumn<Team, String>("Subkamp");
            final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_13 = (TableColumn.CellDataFeatures<Team, String> it_4) -> {
              return it_4.getValue().subcampProperty();
            };
            subcampCol.setCellValueFactory(_function_13);
            ObservableList<TableColumn<Team, ?>> _columns = it_3.getColumns();
            _columns.add(teamCol);
            ObservableList<TableColumn<Team, ?>> _columns_1 = it_3.getColumns();
            _columns_1.add(nameCol);
            ObservableList<TableColumn<Team, ?>> _columns_2 = it_3.getColumns();
            _columns_2.add(likesCol);
            ObservableList<TableColumn<Team, ?>> _columns_3 = it_3.getColumns();
            _columns_3.add(subcampCol);
            it_3.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            it_3.setItems(this.teams);
          };
          TableView<Team> _doubleArrow_6 = ObjectExtensions.<TableView<Team>>operator_doubleArrow(_tableView, _function_9);
          it_2.add(_doubleArrow_6, 1, 1);
        };
        final GridPane table = ObjectExtensions.<GridPane>operator_doubleArrow(_gridPane, _function_2);
        ObservableList<Node> _children = it_1.getChildren();
        _children.add(table);
        ObservableList<Node> _children_1 = it_1.getChildren();
        StackPane _stackPane_1 = new StackPane();
        final Procedure1<StackPane> _function_3 = (StackPane pane) -> {
          BackgroundFill _backgroundFill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY);
          Background _background = new Background(_backgroundFill);
          it_1.setBackground(_background);
          pane.setAlignment(Pos.BOTTOM_RIGHT);
          ObservableList<Node> _children_2 = pane.getChildren();
          Label _label = new Label("time");
          final Procedure1<Label> _function_4 = (Label it_2) -> {
            Insets _insets = new Insets(8);
            it_2.setPadding(_insets);
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            final Runnable _function_5 = () -> {
              try {
                while (true) {
                  {
                    final Runnable _function_6 = () -> {
                      long _time = this.endDate.getTime();
                      long _currentTimeMillis = System.currentTimeMillis();
                      final long timeLeft = (_time - _currentTimeMillis);
                      Date _date = new Date(timeLeft);
                      it_2.setText(timeFormat.format(_date));
                      if ((table.isVisible() && (Duration.ofMillis(timeLeft).toMinutes() <= 10))) {
                        it_2.setMinHeight(8000);
                        it_2.setMaxHeight(8000);
                        it_2.setMinWidth(8000);
                        it_2.setMaxWidth(8000);
                        pane.setAlignment(Pos.CENTER);
                        it_2.setAlignment(Pos.CENTER);
                        String _name = it_2.getFont().getName();
                        Font _font = new Font(_name, 100);
                        it_2.setFont(_font);
                        table.setVisible(false);
                      }
                    };
                    Platform.runLater(_function_6);
                    Thread.sleep(Duration.ofSeconds(1).toMillis());
                  }
                }
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            };
            new Thread(_function_5).start();
          };
          Label _doubleArrow = ObjectExtensions.<Label>operator_doubleArrow(_label, _function_4);
          _children_2.add(_doubleArrow);
        };
        StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane_1, _function_3);
        _children_1.add(_doubleArrow);
      };
      StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane, _function_1);
      Scene _scene = new Scene(_doubleArrow, 800, 600);
      final Procedure1<Scene> _function_2 = (Scene it_1) -> {
        ObservableList<String> _stylesheets = it_1.getStylesheets();
        _stylesheets.add("flatter.css");
      };
      Scene _doubleArrow_1 = ObjectExtensions.<Scene>operator_doubleArrow(_scene, _function_2);
      it.setScene(_doubleArrow_1);
      it.show();
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public static void main(final String[] args) {
    Application.launch(args);
  }
}
