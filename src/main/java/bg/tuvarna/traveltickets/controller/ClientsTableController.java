package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Address;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.service.impl.ClientServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.CLIENT_DIALOG;

public class ClientsTableController implements Initializable {

    @FXML
    private Button buttonView;

    @FXML
    private TableView<Client> tableClients;

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

    @FXML
    void onViewClicked(ActionEvent event) throws IOException {
        // View clients
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CLIENT_DIALOG), AppConfig.getLangBundle());
        DialogPane dialogPane = loader.load();
        ClientDialogController clientDialogController = loader.getController();
        clientDialogController.setMode(ClientDialogController.DialogMode.VIEW);
        Client client = tableClients.getSelectionModel().getSelectedItem();
        clientDialogController.setData(client);
        Dialog dialog = new Dialog();
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initColumns();

        List<Client> clients = JpaOperationsUtil.executeInTransaction(() -> ClientServiceImpl.getInstance().findAll());
        tableClients.setItems(FXCollections.observableArrayList(clients));
    }

    private void initColumns() {
        columnType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final ClientType item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    //some logic must be added here
                } else {
                    //TODO use toString here
                    setText(getLangBundle().getString("label." + item.getName().toString().toLowerCase()));
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
    }

    public void setData() {
    }
}
