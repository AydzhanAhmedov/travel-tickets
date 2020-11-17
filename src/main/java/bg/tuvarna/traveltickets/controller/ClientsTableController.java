package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.entity.Address;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.service.ClientService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
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
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.Constants.CLIENT_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.controller.ClientDialogController.DialogMode.ADD;
import static bg.tuvarna.traveltickets.controller.ClientDialogController.DialogMode.VIEW;

public class ClientsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ClientsTableController.class);

    private final ClientService clientService = ClientServiceImpl.getInstance();

    @FXML
    private BorderPane root;

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

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initColumns();

        List<Client> clients;

        if (AuthServiceImpl.getInstance().loggedUserIsAdmin()) {
            clients = JpaOperationsUtil.execute(em -> clientService.findAllCompaniesAndDistributors());
        } else if (AuthServiceImpl.getInstance().getLoggedClient().getClientType().getName() == ClientType.Enum.DISTRIBUTOR) {
            clients = JpaOperationsUtil.execute(em -> clientService.findAllCashiersForLoggedUser());
        } else {
            LOG.error("Cant display clients for currently logged client type");
            return;
        }

        tableClients.setItems(FXCollections.observableArrayList(clients));
    }

    @FXML
    private void onViewClicked(final ActionEvent event) {
        loadDialog(VIEW, tableClients.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onAddClicked(final ActionEvent event) {
        loadDialog(ADD, null);
    }

    private void loadDialog(final ClientDialogController.DialogMode dialogMode, final Client client) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(CLIENT_DIALOG_FXML_PATH), AppConfig.getLangBundle());
            final DialogPane dialogPane = loader.load();
            final ClientDialogController clientDialogController = loader.getController();

            clientDialogController.injectDialogMode(dialogMode, client, c -> tableClients.getItems().add(c));

            final Dialog<Void> dialog = new UndecoratedDialog<>(root.getParent().getParent(), dialogPane);
            dialog.showAndWait();
        }
        catch (Exception e) {
            LOG.error("Error while trying to load client dialog with mode " + dialogMode.toString() + ": ", e);
        }
    }

    public void setData() {
    }

    private void initColumns() {
        columnType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final ClientType item, final boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ? item.getName().toString() : null);
            }
        });
        columnCity.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Address item, final boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ? item.getCity().getName() : null);
            }
        });
        columnCity.setCellValueFactory(new PropertyValueFactory<>("address"));

        columnType.setCellValueFactory(new PropertyValueFactory<>("clientType"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

}
