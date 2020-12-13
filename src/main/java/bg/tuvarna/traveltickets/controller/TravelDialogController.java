package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.control.ConfirmDialog;
import bg.tuvarna.traveltickets.control.NumberTextField;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.TransportType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.impl.TravelServiceImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_CITY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BUTTON_APPLY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_HOUR_FORMAT_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_ROUTE_DATES_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TICKET_LIMIT_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TICKET_PRICE_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TICKET_QUANTITY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TRAVEL_DETAILS_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TRAVEL_NAME_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_TRAVEL_STATUS_KEY;
import static bg.tuvarna.traveltickets.common.Constants.ROUTE_VIEW_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.UNEXPECTED_ERROR_KEY;
import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.ENDED;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;

public class TravelDialogController extends BaseUndecoratedDialogController {

    private static final Logger LOG = LogManager.getLogger(TravelDialogController.class);
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TravelService travelService = TravelServiceImpl.getInstance();

    private Travel travel;
    private List<HBox> travelRouteXBoxes;

    private Consumer<Travel> onNewTravel;
    private Consumer<Travel> onUpdateTravel;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<TravelType.Enum> typeComboBox;

    @FXML
    private NumberTextField ticketQuantityTextField;

    @FXML
    private NumberTextField currTicketQuantityTextField;

    @FXML
    private TextField ticketPriceTextField;

    @FXML
    private NumberTextField buyLimitTextField;

    @FXML
    private TextArea detailsTextArea;

    @FXML
    private TextField startTimeTextField;

    @FXML
    private DatePicker endDatePicker;

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
        startTimeTextField.setAlignment(Pos.CENTER_RIGHT);
        ticketPriceTextField.setAlignment(Pos.CENTER_RIGHT);

        LOG.debug("Travel dialog loaded.");
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

    public void injectDialogMode(final DialogMode mode, final Travel travel, final Consumer<Travel> onNewTravel, final Consumer<Travel> onUpdateTravel) {
        this.onNewTravel = onNewTravel;
        this.onUpdateTravel = onUpdateTravel;

        if (mode != DialogMode.ADD) setData(travel);
        setDialogMode(mode);
    }

    private boolean dataIsValid() {
        final DialogMode mode = getDialogMode();
        // travel name
        if (mode == DialogMode.ADD && (nameTextField.getText().length() < 3 || nameTextField.getText().length() > 100)) {
            setErrorText(getLangBundle().getString(INVALID_TRAVEL_NAME_KEY));
            return false;
        }
        //travel status
        final TravelStatus.Enum newStatus = statusComboBox.getValue();
        if (mode == DialogMode.EDIT &&
                switch (travel.getTravelStatus().getName()) {
                    case INCOMING -> newStatus == ENDED;
                    case ONGOING -> newStatus != ENDED;
                    default -> newStatus != travel.getTravelStatus().getName();
                }) {

            setErrorText(getLangBundle().getString(INVALID_TRAVEL_STATUS_KEY));
            return false;
        }
        // ticket quantity
        final int newQuantity = ticketQuantityTextField.getText().isBlank() ? 0 : Integer.parseInt(ticketQuantityTextField.getText());
        if (newQuantity == 0 || (getDialogMode() == DialogMode.EDIT && newQuantity < travel.getTicketQuantity())) {
            setErrorText(getLangBundle().getString(INVALID_TICKET_QUANTITY_KEY));
            return false;
        }
        // ticket price
        if (mode == DialogMode.ADD) {
            try {
                final double value = new BigDecimal(ticketPriceTextField.getText()).doubleValue();
                if (value <= 0) {
                    setErrorText(getLangBundle().getString(INVALID_TICKET_PRICE_KEY));
                    return false;
                }
            }
            catch (Exception ignored) {
                setErrorText(getLangBundle().getString(INVALID_TICKET_PRICE_KEY));
                return false;
            }
        }
        // ticket limit
        if (mode == DialogMode.ADD) {
            final int limit = buyLimitTextField.getText().isBlank() ? 0 : Integer.parseInt(buyLimitTextField.getText());
            if (limit < 1) {
                setErrorText(getLangBundle().getString(INVALID_TICKET_LIMIT_KEY));
                return false;
            }
        }
        // travel details
        if (detailsTextArea.getText().isBlank() || detailsTextArea.getText().length() > 500) {
            setErrorText(getLangBundle().getString(INVALID_TRAVEL_DETAILS_KEY));
            return false;
        }
        if (mode == DialogMode.ADD) {
            // hour format
            try {
                HOUR_FORMATTER.parse(startTimeTextField.getText());
            }
            catch (Exception ignored) {
                setErrorText(getLangBundle().getString(INVALID_HOUR_FORMAT_KEY));
                return false;
            }
            // city name
            if (travelRouteXBoxes.stream().map(hb -> ((TextField) hb.getChildren().get(0)).getText()).anyMatch(s -> s == null || s.isBlank())) {
                setErrorText(getLangBundle().getString(BLANK_CITY_KEY));
                return false;
            }
            // routes and arrival dates
            final List<LocalDate> dates = travelRouteXBoxes.stream()
                    .map(hb -> ((DatePicker) hb.getChildren().get(1)).getValue())
                    .collect(Collectors.toList());

            dates.add(endDatePicker.getValue());

            for (int i = 1; i < dates.size(); i++) {
                final LocalDate prev = dates.get(i - 1);
                final LocalDate curr = dates.get(i);

                if (prev == null || curr == null || prev.isAfter(curr)) {
                    setErrorText(getLangBundle().getString(INVALID_ROUTE_DATES_KEY));
                    return false;
                }
            }
        }
        return true;
    }

    private void setErrorText(final String text) {
        LOG.debug("Validation error occurred: {}.", text);
        errorImageView.setVisible(!text.isBlank());
        errorText.setText(text);
    }

    private void readData() {
        travel.setName(nameTextField.getText());
        travel.setDetails(detailsTextArea.getText());
        travel.setTravelType(travelService.findTypeByName(typeComboBox.getValue()));
        travel.setTravelStatus(travelService.findStatusByName(statusComboBox.getValue()));
        travel.setTicketPrice(new BigDecimal(ticketPriceTextField.getText()));
        travel.setTicketQuantity(Integer.parseInt(ticketQuantityTextField.getText()));
        travel.setCurrentTicketQuantity(travel.getTicketQuantity());
        travel.setTicketBuyLimit(Integer.parseInt(buyLimitTextField.getText()));

        final ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        travel.setEndDate(endDatePicker.getValue().atTime(LocalTime.MIN.atOffset(zoneOffset)));

        final OffsetTime startTime = LocalTime.from(HOUR_FORMATTER.parse(startTimeTextField.getText().trim())).atOffset(zoneOffset);
        for (int i = 0; i < travelRouteXBoxes.size(); i++) {
            final TextField cityTextField = (TextField) travelRouteXBoxes.get(i).getChildren().get(0);
            final DatePicker arrivalDatePicker = (DatePicker) travelRouteXBoxes.get(i).getChildren().get(1);
            final @SuppressWarnings("unchecked") ComboBox<TransportType.Enum> transportComboBox = (ComboBox<TransportType.Enum>) travelRouteXBoxes.get(i).getChildren().get(2);

            final TravelRoute travelRoute = travel.getTravelRoutes().get(i);

            travelRoute.setCity(new City(cityTextField.getText()));
            travelRoute.setArrivalDate(arrivalDatePicker.getValue().atTime(LocalTime.MIN.atOffset(zoneOffset)));
            travelRoute.setTransportType(travelService.findTransportTypeByName(transportComboBox.getValue()));

            if (i == 0) travel.setStartDate(arrivalDatePicker.getValue().atTime(startTime));
        }
    }

    private void setData(final Travel travel) {
        this.travel = travel;

        nameTextField.setText(travel.getName());
        ticketQuantityTextField.setText(travel.getTicketQuantity().toString());
        currTicketQuantityTextField.setText(travel.getCurrentTicketQuantity().toString());
        ticketPriceTextField.setText(travel.getTicketPrice().toString());
        buyLimitTextField.setText(travel.getTicketBuyLimit().toString());
        detailsTextArea.setText(travel.getDetails());
        startTimeTextField.setText(HOUR_FORMATTER.format(travel.getStartDate()));
        endDatePicker.setValue(travel.getEndDate().toLocalDate());

        final TravelType.Enum type = travel.getTravelType().getName();
        final TravelStatus.Enum status = travel.getTravelStatus().getName();

        typeComboBox.setValue(type);
        statusComboBox.setValue(status);
        routesTableView.setItems(FXCollections.observableList(travel.getTravelRoutes()));
    }

    @Override
    protected void onViewModeSet() {
        disableFields();
        ticketQuantityTextField.setDisable(true);
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
        currTicketQuantityTextField.setDisable(true);
        statusComboBox.setStyle("-fx-opacity: 1");
        ticketQuantityTextField.textProperty().addListener((o, oldVal, newVal) -> currTicketQuantityTextField.setText(newVal));

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
            try {
                if (!dataIsValid()) {
                    e.consume();
                    return;
                }
                readData();

                new ConfirmDialog(null, getLangBundle().getString("label.dialog.travel_confirmation")).showAndWait()
                        .ifPresent(type -> {
                            if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                                onNewTravel.accept(executeInTransaction(em -> travelService.create(travel)));
                            } else {
                                e.consume();
                            }
                        });
            }
            catch (Exception ex) {
                LOG.error("An error occurred trying to create new travel: ", ex);
                setErrorText(getLangBundle().getString(UNEXPECTED_ERROR_KEY));
                e.consume();
            }
        });
    }

    @Override
    protected void onEditMode() {
        disableFields();

        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;
        ticketQuantityTextField.textProperty().addListener((o, oldVal, newVal) -> {
            final String currVal = currTicketQuantityTextField.getText();
            if (!currVal.isBlank() && !oldVal.isBlank() && !newVal.isBlank()) {
                final int newCurrVal = Integer.parseInt(currVal) + (Integer.parseInt(newVal) - Integer.parseInt(oldVal));
                currTicketQuantityTextField.setText(Integer.toString(newCurrVal));
            }
        });

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            try {
                if (!dataIsValid()) {
                    e.consume();
                    return;
                }

                final TravelStatus.Enum newStatusName = statusComboBox.getValue();
                final String newDetails = detailsTextArea.getText();
                final Integer newTicketQuantity = Integer.parseInt(ticketQuantityTextField.getText());

                onUpdateTravel.accept(executeInTransaction(em -> travelService.updateTravel(travel.getId(), newStatusName, newDetails, newTicketQuantity)));
            }
            catch (Exception ex) {
                LOG.error("An error occurred trying to create new travel: ", ex);
                setErrorText(getLangBundle().getString(UNEXPECTED_ERROR_KEY));
                e.consume();
            }
        });
    }

    private void disableFields() {
        nameTextField.setDisable(true);
        currTicketQuantityTextField.setDisable(true);
        ticketPriceTextField.setDisable(true);
        buyLimitTextField.setDisable(true);
        startTimeTextField.setDisable(true);
        endDatePicker.setDisable(true);
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
                    if (getTableView().getItems().size() < 3)
                        travelRouteXBoxes.get(getIndex()).getChildren().get(3).setDisable(true);
                } else try {
                    final HBox root = FXMLLoader.load(getClass().getResource(ROUTE_VIEW_FXML_PATH), getLangBundle());

                    final TextField cityTextField = (TextField) root.getChildren().get(0);
                    final DatePicker arrivalDatePicker = (DatePicker) root.getChildren().get(1);
                    final @SuppressWarnings("unchecked") ComboBox<TransportType.Enum> transportComboBox = (ComboBox<TransportType.Enum>) root.getChildren().get(2);
                    final Button removeButton = (Button) root.getChildren().get(3);

                    transportComboBox.getItems().setAll(TransportType.Enum.values());

                    cityTextField.setText(item.getCity() != null ? item.getCity().getName() : null);
                    arrivalDatePicker.setValue(item.getArrivalDate() != null ? item.getArrivalDate().toLocalDate() : null);
                    transportComboBox.setValue(item.getTransportType() != null ? item.getTransportType().getName() : TransportType.Enum.BUSS);
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
