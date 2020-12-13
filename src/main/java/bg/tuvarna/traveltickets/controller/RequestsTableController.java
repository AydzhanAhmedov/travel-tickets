package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.control.ConfirmDialog;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.service.RequestService;
import bg.tuvarna.traveltickets.service.impl.RequestServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.ACCEPT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.APPROVED_KEY;
import static bg.tuvarna.traveltickets.common.Constants.DECLINE_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.REJECTED_KEY;

public class RequestsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(RequestsTableController.class);

    private final RequestService requestService = RequestServiceImpl.getInstance();

    @FXML
    private TableView<TravelDistributorRequest> tableRequests;

    @FXML
    private TableColumn<TravelDistributorRequest, Travel> columnTravel;

    @FXML
    private TableColumn<TravelDistributorRequest, RequestStatus> columnStatus;

    @FXML
    private TableColumn<TravelDistributorRequest, Travel> columnStartDate;

    @FXML
    private TableColumn<TravelDistributorRequest, Distributor> columnRequester;

    @FXML
    private TableColumn<TravelDistributorRequest, Client> columnRating;

    @FXML
    private TableColumn<TravelDistributorRequest, TravelDistributorRequest> columnAction;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        initColumns();
        List<TravelDistributorRequest> list = JpaOperationsUtil.execute(em -> requestService.findAll());
        tableRequests.setItems(FXCollections.observableList(list));
    }

    private void initColumns() {
        columnTravel.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                setText(!empty ? item.getName() : null);
            }
        });
        columnTravel.setCellValueFactory(new PropertyValueFactory<>("travel"));

        columnStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final RequestStatus item, final boolean empty) {
                setText(!empty ? item.getName().toString() : null);
            }
        });
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

        columnStartDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                setText(!empty ? AppConfig.getDateTimeFormatter().format(item.getStartDate()) : null);
            }
        });
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("travel"));

        columnRequester.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Distributor item, final boolean empty) {
                if (empty) {
                    setText(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item.getName());
                    setOnMouseClicked(event -> LOG.debug("TODO add client on this click"));
                }
            }
        });
        columnRequester.setCellValueFactory(new PropertyValueFactory<>("distributor"));

        columnAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelDistributorRequest item, final boolean empty) {
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    final TravelDistributorRequest travelDistributorRequest = getTableView().getItems().get(getIndex());

                    if (travelDistributorRequest.getRequestStatus().getName() == RequestStatus.Enum.APPROVED) {
                        final Button btnApproved = new Button(getLangBundle().getString(APPROVED_KEY));
                        btnApproved.setStyle("-fx-background-color: #23801c");
                        btnApproved.setDisable(true);
                        setGraphic(btnApproved);
                    } else if (travelDistributorRequest.getRequestStatus().getName() == RequestStatus.Enum.REJECTED) {
                        final Button btnRejected = new Button(getLangBundle().getString(REJECTED_KEY));
                        btnRejected.setStyle("-fx-background-color: #800d0d");
                        btnRejected.setDisable(true);
                        setGraphic(btnRejected);
                    } else {
                        final Button btnAccept = new Button(getLangBundle().getString(ACCEPT_BUTTON_KEY));
                        btnAccept.setOnAction(event -> {
                            ConfirmDialog alert = new ConfirmDialog(null, getLangBundle().getString("label.dialog.accept_request"));
                            alert.showAndWait().ifPresent(type -> {
                                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                                    requestService.acceptRequest(travelDistributorRequest);
                                    tableRequests.getItems().set(getIndex(), travelDistributorRequest);
                                }
                            });
                        });

                        final Button btnDecline = new Button(getLangBundle().getString(DECLINE_BUTTON_KEY));
                        btnDecline.setOnAction(event -> {
                            ConfirmDialog alert = new ConfirmDialog(null, getLangBundle().getString("label.dialog.decline_requst"));
                            alert.showAndWait().ifPresent(type -> {
                                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                                    requestService.declineRequest(travelDistributorRequest);
                                    tableRequests.getItems().set(getIndex(), travelDistributorRequest);
                                }
                            });
                        });

                        final HBox hBox = new HBox();
                        hBox.setAlignment(Pos.CENTER);
                        hBox.setSpacing(5);
                        hBox.getChildren().addAll(btnAccept, btnDecline);
                        setGraphic(hBox);
                    }
                }
            }
        });

    }

}
