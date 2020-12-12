package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.TicketService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TicketServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_BUYER_NAME_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BUTTON_APPLY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_EMAIL_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_PHONE_KEY;


public class TicketDialogController extends BaseUndecoratedDialogController {

    private static final Logger LOG = LogManager.getLogger(TicketDialogController.class);

    Ticket ticket;

    AuthService authService = AuthServiceImpl.getInstance();
    TicketService ticketService = TicketServiceImpl.getInstance();

    private Consumer<Ticket> onNewTicket;

    @FXML
    private ImageView errorImage;

    @FXML
    private Text errorText;

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


    private void readData() {
        ticket.setBuyerEmail(buyerEmailTextField.getText());
        ticket.setBuyerName(buyerNameTextField.getText());
        ticket.setBuyerPhone(buyerPhoneTextField.getText());

    }

    private boolean validate() {

        if (buyerNameTextField.getText().isBlank()) {
            setErrorText(getLangBundle().getString(BLANK_BUYER_NAME_KEY));
            return false;
        }

        if (buyerPhoneTextField.getText().length() < 6) {
            setErrorText(getLangBundle().getString(INVALID_PHONE_KEY));
            return false;
        }

        if (!Pattern.compile("^(.+)@(.+)$").matcher(buyerEmailTextField.getText()).matches()) {
            setErrorText(getLangBundle().getString(INVALID_EMAIL_KEY));
            return false;
        }

        return true;
    }

    private void setErrorText(final String text) {
        LOG.debug("Error occurred" + text);
        errorImage.setVisible(!text.isBlank());
        errorText.setText(text);
    }

    @Override
    protected void onEditMode() {
        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        okButton.addEventFilter(ActionEvent.ACTION, this::onEditClick);
    }

    private void onAddClick(Event event) {
        if (!validate()) {
            event.consume();
            return;
        }

        readData();
        JpaOperationsUtil.executeInTransaction(em -> ticketService.save(ticket));
        LOG.debug("Ticket created");
    }

    private void onEditClick(Event event) {
        if (!validate()) {
            event.consume();
            return;
        }

        readData();
        JpaOperationsUtil.executeInTransaction(em -> ticketService.save(ticket));
        onNewTicket.accept(ticket);
        LOG.debug("Ticked updated");
    }

    public void injectDialogMode(final DialogMode mode, final Ticket ticket, Consumer<Ticket> onNewTicket) {
        this.ticket = ticket;
        setDialogMode(mode);
        this.onNewTicket = onNewTicket;
        if (mode != DialogMode.ADD) setData(ticket);
    }

    private void setData(final Ticket ticket) {
        this.ticket = ticket;

        buyerEmailTextField.setText(ticket.getBuyerEmail());
        buyerNameTextField.setText(ticket.getBuyerName());
        buyerPhoneTextField.setText(ticket.getBuyerPhone());

        final Travel travel = ticket.getTravel();
        travelNameTextField.setText(travel.getName());

        final Cashier cashier = ticket.getCreatedBy();
        cashierNameTextField.setText(cashier.getName());
        createdAtTextFeild.setText(ticket.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}
