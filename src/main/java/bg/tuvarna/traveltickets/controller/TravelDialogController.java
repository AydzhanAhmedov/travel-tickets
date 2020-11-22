package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.TransportType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.service.TransportTypeService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.TravelStatusService;
import bg.tuvarna.traveltickets.service.TravelTypeService;
import bg.tuvarna.traveltickets.service.impl.TransportTypeServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelStatusServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelTypeServiceImpl;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.BUTTON_APPLY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.ROUTE_VIEW_FXML_PATH;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;

public class TravelDialogController extends BaseUndecoratedDialogController {

    private static final Logger LOG = LogManager.getLogger(TravelDialogController.class);
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TravelService travelService = TravelServiceImpl.getInstance();

    private final TravelStatusService travelStatusService = TravelStatusServiceImpl.getInstance();
    private final TravelTypeService travelTypeService = TravelTypeServiceImpl.getInstance();
    private final TransportTypeService transportTypeService = TransportTypeServiceImpl.getInstance();

    private Travel travel;
    private List<HBox> travelRouteXBoxes;

    private Consumer<Travel> onNewTravel;

    @FXML
    private DialogPane root;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<TravelType.Enum> typeComboBox;

    @FXML
    private TextField ticketQuantityTextField;

    @FXML
    private TextField ticketPriceTextField;

    @FXML
    private TextField buyLimitTextField;

    @FXML
    private TextArea detailsTextArea;

    @FXML
    private TextField startTimeTextField;

    @FXML
    private TableView<TravelRoute> routesTableView;

    @FXML
    private TableColumn<TravelRoute, TravelRoute> routeColumn;

    @FXML
    private Button newRouteButton;

    @FXML
    private ComboBox<TravelStatus.Enum> statusComboBox;

    @FXML
    private ImageView errorImageView;

    @FXML
    private Text errorText;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        initRoutesTable();
        detailsTextArea.setWrapText(true);
        typeComboBox.getItems().setAll(TravelType.Enum.values());
        statusComboBox.getItems().setAll(TravelStatus.Enum.values());

        LOG.debug("Travel dialog loaded.");
    }

    @FXML
    private void onStatusChange(final ActionEvent event) {

    }

    @FXML
    private void onTypeChange(final ActionEvent event) {

    }

    @FXML
    private void onNewRoute(final ActionEvent event) {
        final TravelRoute travelRoute = new TravelRoute();
        travelRoute.setTravel(travel);
        travel.getTravelRoutes().add(travelRoute);
        routesTableView.getItems().add(travelRoute);
        if (routesTableView.getItems().size() > 2) {
            travelRouteXBoxes.get(0).getChildren().get(3).setDisable(false);
            travelRouteXBoxes.get(1).getChildren().get(3).setDisable(false);
        }
    }

    public void injectDialogMode(final DialogMode mode, final Travel travel, final Consumer<Travel> onNewTravel) {
        this.onNewTravel = onNewTravel;
        setDialogMode(mode);

        if (mode != DialogMode.ADD) setData(travel);
    }

    private boolean dataIsValid() {
        return true;
    }

    private void readData() {
        travel.setName(nameTextField.getText());
        travel.setDetails(detailsTextArea.getText());
        travel.setTravelType(travelTypeService.findByName(typeComboBox.getValue()));
        travel.setTravelStatus(travelStatusService.findByName(statusComboBox.getValue()));
        travel.setTicketPrice(new BigDecimal(ticketPriceTextField.getText()));
        travel.setTicketQuantity(Integer.parseInt(ticketQuantityTextField.getText()));
        travel.setCurrentTicketQuantity(travel.getTicketQuantity());
        travel.setTicketBuyLimit(Integer.parseInt(buyLimitTextField.getText()));

        final OffsetTime startTime = LocalTime.from(HOUR_FORMATTER.parse(startTimeTextField.getText().trim())).atOffset(OffsetDateTime.now().getOffset());
        for (int i = 0; i < travelRouteXBoxes.size(); i++) {
            final TextField cityTextField = (TextField) travelRouteXBoxes.get(i).getChildren().get(0);
            final DatePicker arrivalDatePicker = (DatePicker) travelRouteXBoxes.get(i).getChildren().get(1);
            final @SuppressWarnings("unchecked") ComboBox<TransportType.Enum> transportComboBox = (ComboBox<TransportType.Enum>) travelRouteXBoxes.get(i).getChildren().get(2);

            final TravelRoute travelRoute = travel.getTravelRoutes().get(i);

            travelRoute.setCity(new City(cityTextField.getText()));
            travelRoute.setArrivalDate(arrivalDatePicker.getValue().atTime(startTime));
            travelRoute.setTransportType(transportTypeService.findByName(transportComboBox.getValue()));

            if (i == 0) travel.setStartDate(travelRoute.getArrivalDate());
            else if (i == travelRouteXBoxes.size() - 1) travel.setEndDate(travelRoute.getArrivalDate());
        }
    }

    private void setData(final Travel travel) {
        this.travel = travel;

        nameTextField.setText(travel.getName());
        ticketQuantityTextField.setText(travel.getTicketQuantity().toString());
        ticketPriceTextField.setText(travel.getTicketPrice().toString());
        buyLimitTextField.setText(travel.getTicketBuyLimit().toString());
        detailsTextArea.setText(travel.getDetails());
        startTimeTextField.setText(String.valueOf(travel.getStartDate().getHour()));

        final TravelType.Enum type = travel.getTravelType().getName();
        final TravelStatus.Enum status = travel.getTravelStatus().getName();

        typeComboBox.setValue(type);
        statusComboBox.setValue(status);
        routesTableView.setItems(FXCollections.observableList(travel.getTravelRoutes()));
    }

    @Override
    protected void onViewModeSet() {
        disableFields();
        detailsTextArea.setEditable(false);
        detailsTextArea.setFocusTraversable(false);
        detailsTextArea.setStyle("-fx-opacity: 1");
        statusComboBox.setDisable(true);
        statusComboBox.setStyle("-fx-opacity: 1");
    }

    @Override
    protected void onAddModeSet() {
        travel = new Travel();
        typeComboBox.setValue(TravelType.Enum.ADVENTURE);
        statusComboBox.setValue(TravelStatus.Enum.INCOMING);
        statusComboBox.setDisable(true);
        statusComboBox.setStyle("-fx-opacity: 1");

        routesTableView.setItems(FXCollections.observableArrayList());
        if (getDialogMode() == DialogMode.ADD) {
            newRouteButton.fire();
            newRouteButton.fire();
        }

        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            if (!dataIsValid()) return;
            readData();
            onNewTravel.accept(executeInTransaction(em -> travelService.create(travel)));
        });
    }

    @Override
    protected void onEditMode() {
        disableFields();

        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
//        okButton.addEventFilter(ActionEvent.ACTION, e -> {
//            onNewTravel.accept(executeInTransaction(em -> travelService.updateTravel(travel, ));
//        });
    }

    private void disableFields() {
        nameTextField.setDisable(true);
        ticketQuantityTextField.setDisable(true);
        ticketPriceTextField.setDisable(true);
        buyLimitTextField.setDisable(true);
        startTimeTextField.setDisable(true);
        typeComboBox.setDisable(true);
        typeComboBox.setStyle("-fx-opacity: 1");
        newRouteButton.setDisable(true);
    }

    private void initRoutesTable() {
        travelRouteXBoxes = new ArrayList<>();
        routeColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final TravelRoute item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else if (travelRouteXBoxes.size() > getIndex() && travelRouteXBoxes.get(getIndex()) != null) {
                    setGraphic(travelRouteXBoxes.get(getIndex()));
                    if (getTableView().getItems().size() < 3) travelRouteXBoxes.get(getIndex()).getChildren().get(3).setDisable(true);
                } else try {
                        final HBox root = FXMLLoader.load(getClass().getResource(ROUTE_VIEW_FXML_PATH), getLangBundle());

                        final TextField cityTextField = (TextField) root.getChildren().get(0);
                        final DatePicker arrivalDatePicker = (DatePicker) root.getChildren().get(1);
                        final @SuppressWarnings("unchecked") ComboBox<TransportType.Enum> transportComboBox = (ComboBox<TransportType.Enum>) root.getChildren().get(2);
                        final Button removeButton = (Button) root.getChildren().get(3);

                        transportComboBox.getItems().setAll(TransportType.Enum.values());

                        cityTextField.setText(item.getCity() != null ? item.getCity().getName() : null);
                        arrivalDatePicker.setValue(item.getArrivalDate() != null ? item.getArrivalDate().toLocalDate() : null);
                        transportComboBox.setValue(item.getTransportType() != null ? item.getTransportType().getName() : null);
                        removeButton.setOnAction(e -> {
                            final int index = travelRouteXBoxes.indexOf(root);
                            travelRouteXBoxes.remove(index);
                            travel.getTravelRoutes().remove(index);
                            getTableView().getItems().remove(index);
                            getTableView().refresh();
                        });

                        if (getDialogMode() != DialogMode.ADD) {
                            cityTextField.setEditable(false);
                            cityTextField.setOpacity(1);
                            arrivalDatePicker.setDisable(true);
                            transportComboBox.setDisable(true);
                            removeButton.setDisable(true);
                        } else if (getTableView().getItems().size() < 3) removeButton.setDisable(true);

                        travelRouteXBoxes.add(root);
                        setGraphic(root);
                    }
                    catch (Exception e) {
                        LOG.error("Error loading route view: ", e);
                    }
                }
            });
        routeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
    }

}
