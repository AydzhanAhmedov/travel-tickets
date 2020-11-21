package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.ACCEPT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.DECLINE_BUTTON_KEY;

public class RequestsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(RequestsTableController.class);

    @FXML
    private TableColumn<TravelDistributorRequest, Travel> columnTravel;

    @FXML
    private TableColumn<TravelDistributorRequest, RequestStatus> columnStatus;

    @FXML
    private TableColumn<TravelDistributorRequest, Travel> columnStartDate;

    @FXML
    private TableColumn<TravelDistributorRequest, Client> columnRequester;

    @FXML
    private TableColumn<TravelDistributorRequest, Client> columnRating;

    @FXML
    private TableColumn<TravelDistributorRequest, TravelDistributorRequest> columnAction;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        initColumns();
    }

    private void initColumns() {
        columnTravel.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                setText(item.getName());
            }
        });
        columnTravel.setCellValueFactory(new PropertyValueFactory<>("travel"));

        columnStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final RequestStatus item, final boolean empty) {
                setText(item.getName().toString());
            }
        });
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

        columnStartDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                setText(item.getStartDate().toString());
            }
        });
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("travel"));

//        columnRequester.setCellFactory(col -> new TableCell<>(){
//            @Override
//            protected void updateItem(final Client item, final boolean empty) {
//                setText(item.getName());
//            }
//        });
//        columnRequester.setCellValueFactory(new PropertyValueFactory<>("user"));

        columnAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelDistributorRequest item, final boolean empty) {
                final TravelDistributorRequest TravelDistributorRequest = getTableView().getItems().get(getIndex());
                final Button btnAccept = new Button(getLangBundle().getString(ACCEPT_BUTTON_KEY));
                final Button btnDecline = new Button(getLangBundle().getString(DECLINE_BUTTON_KEY));

                final HBox hBox = new HBox();
                hBox.getChildren().addAll(btnAccept, btnDecline);
                setGraphic(hBox);
            }
        });

    }

}
