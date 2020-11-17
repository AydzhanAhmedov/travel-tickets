package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.getShortDateTimeFormatter;
import static bg.tuvarna.traveltickets.common.Constants.EDIT_BUTTON_KEY;

public class TravelsTableController implements Initializable {

    @FXML
    private TableView<Travel> tableClients;

    @FXML
    private TableColumn<Travel, String> columnName;

    @FXML
    private TableColumn<Travel, TravelStatus> columnStatus;

    @FXML
    private TableColumn<Travel, Travel> columnDates;

    @FXML
    private TableColumn<Travel, Travel> columnRoutes;

    @FXML
    private TableColumn<Travel, Travel> columnAction;

    @FXML
    private Button addTravelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    @FXML
    private void onAddClicked(final ActionEvent event) {

    }

    private void initColumns() {
        columnName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });
        columnStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelStatus item, final boolean empty) {
                super.updateItem(item, empty);
                final String text = empty ? null : getLangBundle().getString("label.travel_status_" + item.getName().toString().toLowerCase());
                setText(text != null ? text.substring(0, 1).toUpperCase() + text.substring(1) : null);
            }
        });
        columnDates.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : getShortDateTimeFormatter().format(item.getStartDate()) + " - " + getShortDateTimeFormatter().format(item.getEndDate()));
            }
        });
        columnAction.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(final Travel item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    final Travel travel = getTableView().getItems().get(getIndex());
                    final Button btn = new Button(getLangBundle().getString(EDIT_BUTTON_KEY));

                    btn.getStylesheets().addAll(addTravelButton.getStylesheets());
                    btn.getStyleClass().addAll(addTravelButton.getStyleClass());
                    btn.setOnAction(event -> {
                    });

                    setGraphic(btn);
                }
            }
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("travelStatus"));
    }

}
