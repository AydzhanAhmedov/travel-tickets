package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelServiceImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.getShortDateTimeFormatter;
import static bg.tuvarna.traveltickets.common.Constants.EDIT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.REQUEST_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SELL_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.TICKET_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.ADD;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.EDIT;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.VIEW;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.execute;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;
import static java.util.stream.Collectors.toSet;

public class TravelsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(TravelsTableController.class);

    private final TravelService travelService = TravelServiceImpl.getInstance();
    private final AuthService authService = AuthServiceImpl.getInstance();

    private Set<Long> travelsWithRequests;

    @FXML
    private BorderPane root;

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

        if (authService.getLoggedClientTypeName() != COMPANY) {
            addTravelButton.setVisible(false);
        }


        tableClients.setRowFactory(tv -> {
            final TableRow<Travel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty())
                    loadDialog(VIEW, tableClients.getSelectionModel().getSelectedItem());
            });
            return row;
        });

        executeInTransaction(em -> ClientRepositoryImpl.getInstance().findAllByClientTypeId(2L));

        tableClients.setItems(FXCollections.observableList(execute(em -> {
            final List<Travel> travels = travelService.findAll();

            if (authService.getLoggedClientTypeName() == DISTRIBUTOR) {
                travelsWithRequests = travelService.findAllRequests().stream().map(r -> r.getTravel().getId()).collect(toSet());
            } else {
                travelsWithRequests = Collections.emptySet();
            }

            return travels;
        })));
    }

    @FXML
    private void onAddClicked(final ActionEvent event) {
        loadDialog(ADD, null);
    }

    private void loadDialog(final DialogMode dialogMode, final Travel travel) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(TRAVEL_DIALOG_FXML_PATH), getLangBundle());
            final DialogPane dialogPane = loader.load();
            final TravelDialogController travelDialogController = loader.getController();

            travelDialogController.injectDialogMode(dialogMode, travel, t -> tableClients.getItems().add(t));

            final Dialog<Void> dialog = new UndecoratedDialog<>(root.getParent().getParent(), dialogPane);
            dialog.showAndWait();
        }
        catch (Exception e) {
            LOG.error("Error while trying to load travel dialog with mode " + dialogMode.toString() + ": ", e);
        }
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
                setText(!empty ? item.getName().toString() : null);
            }
        });
        columnType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelType item, final boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ? item.getName().toString() : null);
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
                    final Button btn = new Button();

                    btn.setText(getLangBundle().getString(switch (authService.getLoggedClientTypeName()) {
                        case DISTRIBUTOR -> {
                            btn.setOnAction(e -> {
                                executeInTransaction(em -> travelService.createRequest(item));
                                travelsWithRequests.add(item.getId());
                                getTableView().getItems().set(getIndex(), item);
                            });
                            yield REQUEST_BUTTON_KEY;
                        }
                        case CASHIER -> {
                            btn.setOnAction(e -> onTicketBuy(e, item));
                            yield SELL_BUTTON_KEY; // TODO: implement with tickets functionality
                        }
                        default -> {
                            btn.setOnAction(e -> loadDialog(EDIT, getTableView().getItems().get(getIndex())));
                            yield EDIT_BUTTON_KEY;
                        }
                    }));
                    btn.getStylesheets().addAll(addTravelButton.getStylesheets());
                    btn.getStyleClass().addAll(addTravelButton.getStyleClass());

                    if (travelsWithRequests.contains(item.getId())) btn.setDisable(true);

                    setGraphic(btn);
                }
            }

            private void onTicketBuy(final ActionEvent actionEvent, Travel item) {
                try {
                    final FXMLLoader loader = new FXMLLoader(getClass().getResource(TICKET_DIALOG_FXML_PATH), getLangBundle());
                    final DialogPane dialogPane;
                    dialogPane = loader.load();

                    final TicketDialogController ticketDialogController = loader.getController();
                    Ticket ticket = new Ticket();
                    ticket.setTravel(item);
                    ticketDialogController.injectDialogMode(ADD, ticket);
                    final Dialog<Void> dialog = new UndecoratedDialog<>(root.getParent().getParent(), dialogPane);
                    dialog.showAndWait();

                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("travelStatus"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("travelType"));
        columnDates.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        columnAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
    }

}
