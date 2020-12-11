package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.service.TicketService;
import bg.tuvarna.traveltickets.service.impl.TicketServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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

import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.EDIT_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.TICKET_DIALOG_FXML_PATH;

public class TicketsTableController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(TicketsTableController.class);

    private final TicketService ticketService = TicketServiceImpl.getInstance();

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Ticket> tableTickets;

    @FXML
    private TableColumn<Ticket, Travel> columnTravel;

    @FXML
    private TableColumn<Ticket, String> columnBuyerName;

    @FXML
    private TableColumn<Ticket, String> columnBuyerPhone;

    @FXML
    private TableColumn<Ticket, String> columnBuyerEmail;

    @FXML
    private TableColumn<Ticket, Cashier> columnSoldBy;

    @FXML
    private TableColumn<Ticket, OffsetDateTime> columnDateOfSale;

    @FXML
    private TableColumn<Ticket, Ticket> columnAction;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initColumns();

        List<Ticket> ticketList = ticketService.findAll();
        tableTickets.setItems(FXCollections.observableList(JpaOperationsUtil.execute(em -> ticketService.findAll())));
    }

    private void initColumns() {
        columnTravel.setCellFactory(cel -> new TableCell<>() {
            @Override
            protected void updateItem(final Travel item, final boolean empty) {
                setText(!empty ? item.getName() : null);
            }
        });
        columnTravel.setCellValueFactory(new PropertyValueFactory<>("travel"));

        columnBuyerName.setCellValueFactory(new PropertyValueFactory<>("buyerName"));
        columnBuyerPhone.setCellValueFactory(new PropertyValueFactory<>("buyerPhone"));
        columnBuyerEmail.setCellValueFactory(new PropertyValueFactory<>("buyerEmail"));

        columnSoldBy.setCellFactory(cel -> new TableCell<>() {
            @Override
            protected void updateItem(final Cashier item, final boolean empty) {
                setText(!empty ? item.getName() : null);
            }
        });
        columnSoldBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        columnDateOfSale.setCellFactory(cell -> new TableCell<>() {
            @Override
            protected void updateItem(final OffsetDateTime item, final boolean empty) {
                setText(!empty ? item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
            }
        });
        columnDateOfSale.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        columnAction.setCellFactory(cel -> new TableCell<>() {
            @Override
            protected void updateItem(final Ticket item, final boolean empty) {
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    final Button btn = new Button(getLangBundle().getString(EDIT_BUTTON_KEY));
                    btn.setOnAction(e -> {
                        loadDialog(BaseUndecoratedDialogController.DialogMode.EDIT, item, c -> tableTickets.getItems().set(getIndex(), item));
                        //getTableView().getItems().set(getIndex(), item);
                    });

                    setGraphic(btn);
                }
            }
        });
        columnAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));

        tableTickets.setRowFactory(tv -> {
            final TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty())
                    loadDialog(BaseUndecoratedDialogController.DialogMode.VIEW, tableTickets.getSelectionModel().getSelectedItem(),null);
            });
            return row;
        });
    }

    private void loadDialog(final BaseUndecoratedDialogController.DialogMode dialogMode, final Ticket ticket, Consumer<Ticket> consumer) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(TICKET_DIALOG_FXML_PATH), getLangBundle());
            final DialogPane dialogPane = loader.load();
            final TicketDialogController ticketDialogController = loader.getController();

            ticketDialogController.injectDialogMode(dialogMode, ticket, consumer);

            final Dialog<Void> dialog = new UndecoratedDialog<>(root.getParent().getParent(), dialogPane);
            dialog.showAndWait();
        }
        catch (Exception e) {
            LOG.error("Error while trying to load ticket dialog with mode " + dialogMode.toString() + ": ", e);
        }
    }
}
