package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Address;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.service.impl.ClientServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClientsTableController implements Initializable {

    @FXML
    private TableView tableClients;

    @FXML
    private TableColumn<Client, ClientType> columnType;

    @FXML
    private TableColumn<Client, String> columnName;

    @FXML
    private TableColumn<Client, String> columnPhone;

    @FXML
    private TableColumn<Client, Address> columnCity;

    @FXML
    private TableColumn<Client, Address> columnRegion;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {


        columnType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final ClientType item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    //some logic must be added here
                } else {
                    setText(AppConfig.getLangBundle().getString("label." + item.getName().toString().toLowerCase()));
                }
            }
        });

        columnType.setCellValueFactory(new PropertyValueFactory<>("clientType"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        columnCity.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Address item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // some logic
                } else {
                    setText(item.getCity().getName());
                }
            }
        });

        columnRegion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Address item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // some logic
                } else {
                    setText(item.getCity().getRegion().getName());
                }
            }
        });

        PropertyValueFactory addressProperty = new PropertyValueFactory("address");
        columnCity.setCellValueFactory(addressProperty);
        columnRegion.setCellValueFactory(addressProperty);

        List<Client> clients = JpaOperationsUtil.executeInTransaction(() -> ClientServiceImpl.getInstance().findAll());
        tableClients.setItems(FXCollections.observableArrayList(clients));

    }

    public void setData() {
    }
}
