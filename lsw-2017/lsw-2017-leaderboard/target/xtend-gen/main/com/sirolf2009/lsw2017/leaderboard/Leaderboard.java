package com.sirolf2009.lsw2017.leaderboard;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.sirolf2009.lsw2017.leaderboard.model.Team;
import java.util.Date;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import xtendfx.FXApp;

@FXApp
@SuppressWarnings("all")
public class Leaderboard extends Application {
  private final ObservableList<Team> testData = FXCollections.<Team>observableArrayList(new Team(42, "sirolf2009", "Krummi", 0), new Team((-100), "Ólavur Riddararós", "Týr", 0), new Team(1234, "Rotlaust Tre Fell", "Wardruna", 0));
  
  @Override
  public void start(final Stage it) throws Exception {
    it.setTitle("LSW 2017 leaderboard");
    final Cluster cluster = Cluster.builder().addContactPoint("localhost").build();
    final Session session = cluster.connect("lsw2017");
    final Runnable _function = () -> {
      while (true) {
        try {
          final Function1<Row, Team> _function_1 = (Row it_1) -> {
            int _int = it_1.getInt("points");
            String _string = it_1.getString("teamname");
            long _time = it_1.<Date>get("lastcheckedin", Date.class).getTime();
            return new Team(_int, _string, "TODO", _time);
          };
          final Consumer<Team> _function_2 = (Team it_1) -> {
            final Function1<Team, Boolean> _function_3 = (Team data) -> {
              return Boolean.valueOf(data.getName().equals(it_1.getName()));
            };
            final Team existing = IterableExtensions.<Team>findFirst(this.testData, _function_3);
            if ((existing == null)) {
              this.testData.add(it_1);
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
    GridPane _gridPane = new GridPane();
    final Procedure1<GridPane> _function_1 = (GridPane it_1) -> {
      ObservableList<ColumnConstraints> _columnConstraints = it_1.getColumnConstraints();
      ColumnConstraints _columnConstraints_1 = new ColumnConstraints();
      final Procedure1<ColumnConstraints> _function_2 = (ColumnConstraints it_2) -> {
        it_2.setPercentWidth(10);
      };
      ColumnConstraints _doubleArrow = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_1, _function_2);
      _columnConstraints.add(_doubleArrow);
      ObservableList<ColumnConstraints> _columnConstraints_2 = it_1.getColumnConstraints();
      ColumnConstraints _columnConstraints_3 = new ColumnConstraints();
      final Procedure1<ColumnConstraints> _function_3 = (ColumnConstraints it_2) -> {
        it_2.setPercentWidth(80);
      };
      ColumnConstraints _doubleArrow_1 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_3, _function_3);
      _columnConstraints_2.add(_doubleArrow_1);
      ObservableList<ColumnConstraints> _columnConstraints_4 = it_1.getColumnConstraints();
      ColumnConstraints _columnConstraints_5 = new ColumnConstraints();
      final Procedure1<ColumnConstraints> _function_4 = (ColumnConstraints it_2) -> {
        it_2.setPercentWidth(10);
      };
      ColumnConstraints _doubleArrow_2 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_5, _function_4);
      _columnConstraints_4.add(_doubleArrow_2);
      ObservableList<RowConstraints> _rowConstraints = it_1.getRowConstraints();
      RowConstraints _rowConstraints_1 = new RowConstraints();
      final Procedure1<RowConstraints> _function_5 = (RowConstraints it_2) -> {
        it_2.setPercentHeight(10);
      };
      RowConstraints _doubleArrow_3 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_1, _function_5);
      _rowConstraints.add(_doubleArrow_3);
      ObservableList<RowConstraints> _rowConstraints_2 = it_1.getRowConstraints();
      RowConstraints _rowConstraints_3 = new RowConstraints();
      final Procedure1<RowConstraints> _function_6 = (RowConstraints it_2) -> {
        it_2.setPercentHeight(80);
      };
      RowConstraints _doubleArrow_4 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_3, _function_6);
      _rowConstraints_2.add(_doubleArrow_4);
      ObservableList<RowConstraints> _rowConstraints_4 = it_1.getRowConstraints();
      RowConstraints _rowConstraints_5 = new RowConstraints();
      final Procedure1<RowConstraints> _function_7 = (RowConstraints it_2) -> {
        it_2.setPercentHeight(10);
      };
      RowConstraints _doubleArrow_5 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_5, _function_7);
      _rowConstraints_4.add(_doubleArrow_5);
      TableView<Team> _tableView = new TableView<Team>();
      final Procedure1<TableView<Team>> _function_8 = (TableView<Team> it_2) -> {
        final TableColumn<Team, Number> likesCol = new TableColumn<Team, Number>("Vind ik leuks");
        final Callback<TableColumn.CellDataFeatures<Team, Number>, ObservableValue<Number>> _function_9 = (TableColumn.CellDataFeatures<Team, Number> it_3) -> {
          return it_3.getValue().likesProperty();
        };
        likesCol.setCellValueFactory(_function_9);
        final TableColumn<Team, String> nameCol = new TableColumn<Team, String>("Naam");
        final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_10 = (TableColumn.CellDataFeatures<Team, String> it_3) -> {
          return it_3.getValue().nameProperty();
        };
        nameCol.setCellValueFactory(_function_10);
        final TableColumn<Team, String> subcampCol = new TableColumn<Team, String>("Subkamp");
        final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_11 = (TableColumn.CellDataFeatures<Team, String> it_3) -> {
          return it_3.getValue().subcampProperty();
        };
        subcampCol.setCellValueFactory(_function_11);
        ObservableList<TableColumn<Team, ?>> _columns = it_2.getColumns();
        _columns.add(likesCol);
        ObservableList<TableColumn<Team, ?>> _columns_1 = it_2.getColumns();
        _columns_1.add(nameCol);
        ObservableList<TableColumn<Team, ?>> _columns_2 = it_2.getColumns();
        _columns_2.add(subcampCol);
        it_2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        it_2.setItems(this.testData);
      };
      TableView<Team> _doubleArrow_6 = ObjectExtensions.<TableView<Team>>operator_doubleArrow(_tableView, _function_8);
      it_1.add(_doubleArrow_6, 1, 1);
    };
    GridPane _doubleArrow = ObjectExtensions.<GridPane>operator_doubleArrow(_gridPane, _function_1);
    Scene _scene = new Scene(_doubleArrow, 800, 600);
    final Procedure1<Scene> _function_2 = (Scene it_1) -> {
      ObservableList<String> _stylesheets = it_1.getStylesheets();
      _stylesheets.add("flatter.css");
    };
    Scene _doubleArrow_1 = ObjectExtensions.<Scene>operator_doubleArrow(_scene, _function_2);
    it.setScene(_doubleArrow_1);
    it.show();
  }
  
  public static void main(final String[] args) {
    Application.launch(args);
  }
}
