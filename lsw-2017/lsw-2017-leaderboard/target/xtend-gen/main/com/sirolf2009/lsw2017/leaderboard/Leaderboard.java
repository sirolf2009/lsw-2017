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
    final Runnable _function = new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            final Function1<Row, Team> _function = new Function1<Row, Team>() {
              @Override
              public Team apply(final Row it) {
                int _int = it.getInt("points");
                String _string = it.getString("teamname");
                long _time = it.<Date>get("lastcheckedin", Date.class).getTime();
                return new Team(_int, _string, "TODO", _time);
              }
            };
            final Consumer<Team> _function_1 = new Consumer<Team>() {
              @Override
              public void accept(final Team it) {
                final Function1<Team, Boolean> _function = new Function1<Team, Boolean>() {
                  @Override
                  public Boolean apply(final Team data) {
                    return Boolean.valueOf(data.getName().equals(it.getName()));
                  }
                };
                final Team existing = IterableExtensions.<Team>findFirst(Leaderboard.this.testData, _function);
                if ((existing == null)) {
                  Leaderboard.this.testData.add(it);
                } else {
                  existing.setLikes(it.getLikes());
                }
              }
            };
            ListExtensions.<Row, Team>map(session.execute("SELECT * FROM teams").all(), _function).forEach(_function_1);
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
      }
    };
    new Thread(_function).start();
    GridPane _gridPane = new GridPane();
    final Procedure1<GridPane> _function_1 = new Procedure1<GridPane>() {
      @Override
      public void apply(final GridPane it) {
        ObservableList<ColumnConstraints> _columnConstraints = it.getColumnConstraints();
        ColumnConstraints _columnConstraints_1 = new ColumnConstraints();
        final Procedure1<ColumnConstraints> _function = new Procedure1<ColumnConstraints>() {
          @Override
          public void apply(final ColumnConstraints it) {
            it.setPercentWidth(10);
          }
        };
        ColumnConstraints _doubleArrow = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_1, _function);
        _columnConstraints.add(_doubleArrow);
        ObservableList<ColumnConstraints> _columnConstraints_2 = it.getColumnConstraints();
        ColumnConstraints _columnConstraints_3 = new ColumnConstraints();
        final Procedure1<ColumnConstraints> _function_1 = new Procedure1<ColumnConstraints>() {
          @Override
          public void apply(final ColumnConstraints it) {
            it.setPercentWidth(80);
          }
        };
        ColumnConstraints _doubleArrow_1 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_3, _function_1);
        _columnConstraints_2.add(_doubleArrow_1);
        ObservableList<ColumnConstraints> _columnConstraints_4 = it.getColumnConstraints();
        ColumnConstraints _columnConstraints_5 = new ColumnConstraints();
        final Procedure1<ColumnConstraints> _function_2 = new Procedure1<ColumnConstraints>() {
          @Override
          public void apply(final ColumnConstraints it) {
            it.setPercentWidth(10);
          }
        };
        ColumnConstraints _doubleArrow_2 = ObjectExtensions.<ColumnConstraints>operator_doubleArrow(_columnConstraints_5, _function_2);
        _columnConstraints_4.add(_doubleArrow_2);
        ObservableList<RowConstraints> _rowConstraints = it.getRowConstraints();
        RowConstraints _rowConstraints_1 = new RowConstraints();
        final Procedure1<RowConstraints> _function_3 = new Procedure1<RowConstraints>() {
          @Override
          public void apply(final RowConstraints it) {
            it.setPercentHeight(10);
          }
        };
        RowConstraints _doubleArrow_3 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_1, _function_3);
        _rowConstraints.add(_doubleArrow_3);
        ObservableList<RowConstraints> _rowConstraints_2 = it.getRowConstraints();
        RowConstraints _rowConstraints_3 = new RowConstraints();
        final Procedure1<RowConstraints> _function_4 = new Procedure1<RowConstraints>() {
          @Override
          public void apply(final RowConstraints it) {
            it.setPercentHeight(80);
          }
        };
        RowConstraints _doubleArrow_4 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_3, _function_4);
        _rowConstraints_2.add(_doubleArrow_4);
        ObservableList<RowConstraints> _rowConstraints_4 = it.getRowConstraints();
        RowConstraints _rowConstraints_5 = new RowConstraints();
        final Procedure1<RowConstraints> _function_5 = new Procedure1<RowConstraints>() {
          @Override
          public void apply(final RowConstraints it) {
            it.setPercentHeight(10);
          }
        };
        RowConstraints _doubleArrow_5 = ObjectExtensions.<RowConstraints>operator_doubleArrow(_rowConstraints_5, _function_5);
        _rowConstraints_4.add(_doubleArrow_5);
        TableView<Team> _tableView = new TableView<Team>();
        final Procedure1<TableView<Team>> _function_6 = new Procedure1<TableView<Team>>() {
          @Override
          public void apply(final TableView<Team> it) {
            final TableColumn<Team, Number> likesCol = new TableColumn<Team, Number>("Vind ik leuks");
            final Callback<TableColumn.CellDataFeatures<Team, Number>, ObservableValue<Number>> _function = new Callback<TableColumn.CellDataFeatures<Team, Number>, ObservableValue<Number>>() {
              @Override
              public ObservableValue<Number> call(final TableColumn.CellDataFeatures<Team, Number> it) {
                return it.getValue().likesProperty();
              }
            };
            likesCol.setCellValueFactory(_function);
            final TableColumn<Team, String> nameCol = new TableColumn<Team, String>("Naam");
            final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_1 = new Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(final TableColumn.CellDataFeatures<Team, String> it) {
                return it.getValue().nameProperty();
              }
            };
            nameCol.setCellValueFactory(_function_1);
            final TableColumn<Team, String> subcampCol = new TableColumn<Team, String>("Subkamp");
            final Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>> _function_2 = new Callback<TableColumn.CellDataFeatures<Team, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(final TableColumn.CellDataFeatures<Team, String> it) {
                return it.getValue().subcampProperty();
              }
            };
            subcampCol.setCellValueFactory(_function_2);
            ObservableList<TableColumn<Team, ?>> _columns = it.getColumns();
            _columns.add(likesCol);
            ObservableList<TableColumn<Team, ?>> _columns_1 = it.getColumns();
            _columns_1.add(nameCol);
            ObservableList<TableColumn<Team, ?>> _columns_2 = it.getColumns();
            _columns_2.add(subcampCol);
            it.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            it.setItems(Leaderboard.this.testData);
          }
        };
        TableView<Team> _doubleArrow_6 = ObjectExtensions.<TableView<Team>>operator_doubleArrow(_tableView, _function_6);
        it.add(_doubleArrow_6, 1, 1);
      }
    };
    GridPane _doubleArrow = ObjectExtensions.<GridPane>operator_doubleArrow(_gridPane, _function_1);
    Scene _scene = new Scene(_doubleArrow, 800, 600);
    final Procedure1<Scene> _function_2 = new Procedure1<Scene>() {
      @Override
      public void apply(final Scene it) {
        ObservableList<String> _stylesheets = it.getStylesheets();
        _stylesheets.add("flatter.css");
      }
    };
    Scene _doubleArrow_1 = ObjectExtensions.<Scene>operator_doubleArrow(_scene, _function_2);
    it.setScene(_doubleArrow_1);
    it.show();
  }
  
  public static void main(final String[] args) {
    Application.launch(args);
  }
}
