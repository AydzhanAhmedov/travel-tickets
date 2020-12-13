package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.RequestService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.RequestServiceImpl;
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
import java.util.Map;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.getShortDateTimeFormatter;
import static bg.tuvarna.traveltickets.common.Constants.APPROVED_KEY;
import static bg.tuvarna.traveltickets.common.Constants.EDIT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.REJECTED_KEY;
import static bg.tuvarna.traveltickets.common.Constants.REQUEST_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SELL_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SOLD_OUT_KEY;
import static bg.tuvarna.traveltickets.common.Constants.TICKET_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.ADD;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.EDIT;
import static bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController.DialogMode.VIEW;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.execute;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;
import static java.util.stream.Collectors.toMap;

public class TravelsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(TravelsTableController.class);

    private final TravelService travelService = TravelServiceImpl.getInstance();
    private final RequestService requestService = RequestServiceImpl.getInstance();
    private final AuthService authService = AuthServiceImpl.getInstance();

    private Map<Long, RequestStatus.Enum> requestStatusByTravelId;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Travel> tableTravels;

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

        tableTravels.setRowFactory(tv -> {
            final TableRow<Travel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty())
                    loadDialog(VIEW, tableTravels.getSelectionModel().getSelectedItem(), tableTravels.getSelectionModel().getSelectedIndex());
            });
            return row;
        });
        tableTravels.setItems(FXCollections.observableList(execute(em -> {
            final List<Travel> travels = travelService.findAll();

            if (authService.getLoggedClientTypeName() == DISTRIBUTOR) {
                requestStatusByTravelId = requestService.findAll().stream().collect(toMap(r -> r.getTravel().getId(), r -> r.getRequestStatus().getName()));
            } else {
                requestStatusByTravelId = Collections.emptyMap();
            }

            return travels;
        })));
    }

    @FXML
    private void onAddClicked(final ActionEvent event) {
        loadDialog(ADD, null, null);
    }

    private void loadDialog(final DialogMode dialogMode, final Travel travel, final Integer index) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(TRAVEL_DIALOG_FXML_PATH), getLangBundle());
            final DialogPane dialogPane = loader.load();
            final TravelDialogController travelDialogController = loader.getController();

            travelDialogController.injectDialogMode(dialogMode, travel, t -> tableTravels.getItems().add(t), t -> tableTravels.getItems().set(index, t));

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
                                executeInTransaction(em -> requestService.createRequest(item));
                                requestStatusByTravelId.put(item.getId(), RequestStatus.Enum.PENDING);
                                getTableView().getItems().set(getIndex(), item);
                            });
                            if (requestStatusByTravelId.get(item.getId()) == RequestStatus.Enum.APPROVED) {
                                btn.setStyle("-fx-background-color: #23801c");
                                yield APPROVED_KEY;
                            } else if (requestStatusByTravelId.get(item.getId()) == RequestStatus.Enum.REJECTED) {
                                btn.setStyle("-fx-background-color: #800d0d");
                                yield REJECTED_KEY;
                            }
                            yield REQUEST_BUTTON_KEY;
                        }
                        case CASHIER -> {
                            if (item.getTravelStatus().getName() == TravelStatus.Enum.INCOMING) {
                                if (item.getCurrentTicketQuantity() > 0)
                                    btn.setOnAction(e -> onTicketBuy(e, item));
                                else {
                                    btn.setStyle("-fx-background-color: #800d0d");
                                    btn.setDisable(true);
                                    yield SOLD_OUT_KEY;
                                }
                            }
                            yield SELL_BUTTON_KEY;
                        }
                        default -> {
                            btn.setOnAction(e -> loadDialog(EDIT, getTableView().getItems().get(getIndex()), getIndex()));
                            yield EDIT_BUTTON_KEY;
                        }
                    }));
                    btn.getStylesheets().addAll(addTravelButton.getStylesheets());
                    btn.getStyleClass().addAll(addTravelButton.getStyleClass());

                    if (requestStatusByTravelId.containsKey(item.getId())) btn.setDisable(true);

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

    private void onTicketBuy(final ActionEvent actionEvent, Travel item) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(TICKET_DIALOG_FXML_PATH), getLangBundle());
            final DialogPane dialogPane;
            dialogPane = loader.load();

            final TicketDialogController ticketDialogController = loader.getController();
            Ticket ticket = new Ticket();
            ticket.setTravel(item);
            ticketDialogController.injectDialogMode(ADD, ticket, null);
            final Dialog<Void> dialog = new UndecoratedDialog<>(root.getParent().getParent(), dialogPane);

            dialog.showAndWait();
            tableTravels.getItems().set(tableTravels.getSelectionModel().getSelectedIndex(), item);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
