package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.TicketService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TicketServiceImpl;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.BUTTON_APPLY_KEY;


public class TicketDialogController extends BaseUndecoratedDialogController {

    Ticket ticket;

    AuthService authService = AuthServiceImpl.getInstance();
    TicketService ticketService = TicketServiceImpl.getInstance();

    @FXML
    private DialogPane root;

    @FXML
    private TextField travelNameTextField;

    @FXML
    private TextField buyerNameTextField;

    @FXML
    private TextField buyerPhoneTextField;

    @FXML
    private TextField buyerEmailTextField;

    @FXML
    private TextField cashierNameTextField;

    @FXML
    private TextField createdAtTextFeild;

    @Override
    protected void onViewModeSet() {
        buyerPhoneTextField.setEditable(false);
        buyerNameTextField.setEditable(false);
        buyerEmailTextField.setEditable(false);
    }

    @Override
    protected void onAddModeSet() {
        Cashier cashier = (Cashier) authService.getLoggedClient();
        cashierNameTextField.setText(cashier.getName());
        travelNameTextField.setText(ticket.getTravel().getName());

        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
        okButton.addEventFilter(ActionEvent.ACTION, this::onAddClick);
    }

    private <T extends Event> void onAddClick(final T t) {

        if (!isDataValid())
            return;

        readData();

        ticketService.save(ticket);
    }

    private void readData() {
        ticket.setBuyerEmail(buyerEmailTextField.getText());
        ticket.setBuyerName(buyerNameTextField.getText());
        ticket.setBuyerPhone(buyerPhoneTextField.getText());

    }

    private boolean isDataValid() {
        // TODO add validator
        return true;
    }

    @Override
    protected void onEditMode() {
        // TODO make buyer fields editable
    }

    public void injectDialogMode(final DialogMode mode, final Ticket ticket) {
        this.ticket = ticket;
        setDialogMode(mode);

        if (mode != DialogMode.ADD) setData(ticket);
    }

    private void setData(final Ticket ticket) {
        this.ticket = ticket;

        buyerEmailTextField.setText(ticket.getBuyerName());
        buyerNameTextField.setText(ticket.getBuyerName());
        buyerPhoneTextField.setText(ticket.getBuyerPhone());

        final Travel travel = ticket.getTravel();
        travelNameTextField.setText(travel.getName());

        final Cashier cashier = ticket.getCreatedBy();
        cashierNameTextField.setText(cashier.getName());
        createdAtTextFeild.setText(ticket.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}
