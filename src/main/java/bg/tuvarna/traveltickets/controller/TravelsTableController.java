package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelServiceImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.getShortDateTimeFormatter;
import static bg.tuvarna.traveltickets.common.Constants.EDIT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.REQUEST_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SELL_BUTTON_KEY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.execute;

public class TravelsTableController extends BaseUndecoratedDialogController {

    private final TravelService travelService = TravelServiceImpl.getInstance();
    private final AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private TableView<Travel> tableClients;

    @FXML
    private TableColumn<Travel, Company> columnCompany;

    @FXML
    private TableColumn<Travel, String> columnName;

    @FXML
    private TableColumn<Travel, TravelStatus> columnStatus;

    @FXML
    private TableColumn<Travel, TravelType> columnType;

    @FXML
    private TableColumn<Travel, Travel> columnDates;

    @FXML
    private TableColumn<Travel, Travel> columnAction;

    @FXML
    private Button addTravelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initColumns();
        tableClients.setRowFactory(tv -> {
            final TableRow<Travel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && !row.isEmpty())
//                    loadDialog(VIEW, tableClients.getSelectionModel().getSelectedItem());
            });
            return row;
        });

        tableClients.setItems(FXCollections.observableList(execute(em -> {
            final List<Travel> travels = travelService.findAll();


            return travels;
        })));
    }

    @FXML
    private void onAddClicked(final ActionEvent event) {

    }

    private void initColumns() {
        if (authService.getLoggedClientTypeName() == COMPANY) columnCompany.setVisible(false);
        else {
            columnCompany.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(final Company item, final boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getName());
                }
            });
            columnCompany.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        }
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
                setText(!empty ? text.substring(0, 1).toUpperCase() + text.substring(1) : null);
            }
        });
        columnType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelType item, final boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ? getLangBundle().getString("label." + item.getName().toString().toLowerCase()) : null);
            }
        });
        columnDates.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : getShortDateTimeFormatter().format(item.getStartDate()) + "\n" + getShortDateTimeFormatter().format(item.getEndDate()));
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
                    final ClientType.Enum clientTypeName = authService.getLoggedClientTypeName();
                    final Button btn = new Button(getLangBundle().getString(switch (clientTypeName) {
                        case DISTRIBUTOR -> REQUEST_BUTTON_KEY;
                        case CASHIER -> SELL_BUTTON_KEY;
                        default -> EDIT_BUTTON_KEY;
                    }));

                    btn.getStylesheets().addAll(addTravelButton.getStylesheets());
                    btn.getStyleClass().addAll(addTravelButton.getStyleClass());
                    btn.setOnAction(e -> {
                    });

                    setGraphic(btn);
                }
            }
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("travelStatus"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("travelType"));
        columnDates.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        columnAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
    }

    @Override
    protected void onViewModeSet() {

    }

    @Override
    protected void onAddModeSet() {

    }

    @Override
    protected void onEditMode() {

    }

}
