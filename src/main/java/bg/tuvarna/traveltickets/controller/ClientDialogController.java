package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.Address;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.entity.Role;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.ClientService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.ClientServiceImpl;
import bg.tuvarna.traveltickets.service.impl.ClientTypeServiceImpl;
import bg.tuvarna.traveltickets.service.impl.RoleServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_CITY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_DESCRIPTION_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_NAME_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_URL_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BUTTON_APPLY_KEY;
import static bg.tuvarna.traveltickets.common.Constants.DESCRIPTION_KEY;
import static bg.tuvarna.traveltickets.common.Constants.EMAIL_USED_KEY;
import static bg.tuvarna.traveltickets.common.Constants.HONORARIUM_KEY;
import static bg.tuvarna.traveltickets.common.Constants.IMAGE_URL_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_EMAIL_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_HONORARIUM_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_PASSWORD_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_PHONE_KEY;
import static bg.tuvarna.traveltickets.common.Constants.INVALID_USERNAME_KEY;
import static bg.tuvarna.traveltickets.common.Constants.USERNAME_USED_KEY;

public class ClientDialogController extends BaseUndecoratedDialogController {

    private final ClientService clientService = ClientServiceImpl.getInstance();
    private final AuthService authService = AuthServiceImpl.getInstance();
    private static final Logger LOG = LogManager.getLogger(ClientDialogController.class);

    private Client client;
    private ClientType.Enum clientType;

    private Consumer<Client> onNewClient;

    @FXML
    private Text errorText;

    @FXML
    private ImageView errorImageView;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label detail1Label;

    @FXML
    private Label detail2Label;

    @FXML
    private ComboBox<ClientType.Enum> clientTypeComboBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField detail1TextField;

    @FXML
    private TextField detail2TextField;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        initClientTypeComboBox();
        configureDetails(clientTypeComboBox.getValue());

        LOG.info("Dialog loaded");
    }

    @FXML
    private void onClientTypeChange(ActionEvent event) {
        configureDetails(clientTypeComboBox.getValue());
    }

    private void initClientTypeComboBox() {
        if (authService.loggedUserIsAdmin())
            clientTypeComboBox.getItems().setAll(List.of(ClientType.Enum.COMPANY, ClientType.Enum.DISTRIBUTOR));
        else
            clientTypeComboBox.getItems().setAll(ClientType.Enum.CASHIER);

        clientTypeComboBox.getSelectionModel().select(0);
    }

    public void injectDialogMode(final DialogMode mode, final Client client, final Consumer<Client> onNewClient) {
        this.onNewClient = onNewClient;
        setDialogMode(mode);

        if (mode != DialogMode.ADD) setData(client);
    }

    private boolean validate() {

        if (!Pattern.compile("^(.+)@(.+)$").matcher(emailTextField.getText()).matches()) {
            setErrorText(getLangBundle().getString(INVALID_EMAIL_KEY));
            return false;
        } else {
            if (authService.findByEmail(emailTextField.getText()) != null) {
                setErrorText(getLangBundle().getString(EMAIL_USED_KEY));
                return false;
            }
        }

        if (usernameTextField.getText().length() < 5) {
            setErrorText(getLangBundle().getString(INVALID_USERNAME_KEY));
            return false;
        } else {
            if (authService.findByUsername(usernameTextField.getText()) != null) {
                setErrorText(getLangBundle().getString(USERNAME_USED_KEY));
                return false;
            }
        }

        if (getDialogMode() == DialogMode.ADD) {
            if (passwordField.getText().length() < 8) {
                setErrorText(getLangBundle().getString(INVALID_PASSWORD_KEY));
                return false;
            }
        }

        if (getDialogMode() == DialogMode.EDIT) {
            if (!passwordField.getText().isBlank() && passwordField.getText().length() < 8) {
                setErrorText(getLangBundle().getString(INVALID_PASSWORD_KEY));
                return false;
            }
        }

        if (nameTextField.getText().isBlank()) {
            setErrorText(getLangBundle().getString(BLANK_NAME_KEY));
            return false;
        }

        if (phoneTextField.getText().length() < 6) {
            setErrorText(getLangBundle().getString(INVALID_PHONE_KEY));
            return false;
        }

        if (cityTextField.getText().isBlank()) {
            setErrorText(getLangBundle().getString(BLANK_CITY_KEY));
            return false;
        }

        if (clientType == ClientType.Enum.CASHIER) {
            try {
                NumberFormat.getInstance().parse(detail1TextField.getText());
            }
            catch (ParseException e) {
                setErrorText(getLangBundle().getString(INVALID_HONORARIUM_KEY));
            }
        }

        if (clientType == ClientType.Enum.COMPANY) {
            if (detail1TextField.getText().isBlank()) {
                setErrorText(getLangBundle().getString(BLANK_URL_KEY));
                return false;
            }

            if (detail2TextField.getText().isBlank()) {
                setErrorText(getLangBundle().getString(BLANK_DESCRIPTION_KEY));
                return false;
            }
        }

        LOG.debug("Validator passed");
        return true;
    }

    private void setErrorText(final String text) {
        LOG.debug("Error occurred" + text);
        errorImageView.setVisible(!text.isBlank());
        errorText.setText(text);
    }

    private void onAddClick(Event event) {
        if (!validate()) {
            event.consume();
            return;
        }

        readData();
        JpaOperationsUtil.executeInTransaction(em -> ClientServiceImpl.getInstance().save(client));
        onNewClient.accept(client);
        System.out.println("data read");

    }

    private void onEditClick(Event event) {
        if (!validate()) {
            event.consume();
            return;
        }

        readData();
        JpaOperationsUtil.executeInTransaction(em -> ClientServiceImpl.getInstance().save(client));
        onNewClient.accept(client);
        System.out.println("data read");
    }

    private void setDetailsInvisible() {
        detail1Label.setVisible(false);
        detail2Label.setVisible(false);
        detail1TextField.setVisible(false);
        detail2TextField.setVisible(false);
    }

    private void configureDetails(ClientType.Enum type) {
        setDetailsInvisible();
        clientType = type;
        switch (type) {
            case CASHIER -> {
                detail1Label.setText(getLangBundle().getString(HONORARIUM_KEY));
                detail1Label.setVisible(true);
                detail1TextField.setVisible(true);
            }
            case COMPANY -> {
                detail1Label.setText(getLangBundle().getString(IMAGE_URL_KEY));
                detail1Label.setVisible(true);
                detail1TextField.setVisible(true);
                detail2Label.setText(getLangBundle().getString(DESCRIPTION_KEY));
                detail2Label.setVisible(true);
                detail2TextField.setVisible(true);
            }
        }
    }

    private void readData() {
        if (client == null) {
            client = switch (clientType) {
                case COMPANY -> new Company();
                case CASHIER -> new Cashier();
                case DISTRIBUTOR -> new Distributor();
            };

            User user = new User();
            user.setRole(RoleServiceImpl.getInstance().findByName(Role.Enum.CLIENT));
            client.setUser(user);

            LOG.debug("New local client created");
        }

        client.setClientType(ClientTypeServiceImpl.getInstance().findByName(clientType));

        City city = new City(cityTextField.getText());
        Address address = new Address(city, addressTextField.getText());
        client.setAddress(address);
        client.setName(nameTextField.getText());
        client.setPhone(phoneTextField.getText());
        client.getUser().setEmail(emailTextField.getText());
        client.getUser().setUsername(usernameTextField.getText());

        if (getDialogMode() == DialogMode.ADD || !passwordField.getText().isBlank())
            client.getUser().setPassword(passwordField.getText());

        if (client instanceof Company company) {
            company.setLogoUrl(detail1TextField.getText());
            company.setDescription(detail2TextField.getText());
        } else if (client instanceof Cashier cashier) {
            cashier.setHonorarium(new BigDecimal(detail1TextField.getText()));
        }

        LOG.debug("Data read");
    }

    private void setData(Client client) {
        this.client = client;

        ClientType.Enum clientType = client.getClientType().getName();
        User user = client.getUser();

        configureDetails(clientType);
        emailTextField.setText(user.getEmail());
        usernameTextField.setText(user.getUsername());
        clientTypeComboBox.getSelectionModel().select(client.getClientType().getName());
        nameTextField.setText(client.getName());
        phoneTextField.setText(client.getPhone());
        addressTextField.setText(client.getAddress().getAddress());
        cityTextField.setText(client.getAddress().getCity().getName());

        if (clientType == ClientType.Enum.COMPANY) {
            Company company = (Company) client;
            detail1TextField.setText(company.getLogoUrl());
            detail2TextField.setText(company.getDescription());
        }
        if (clientType == ClientType.Enum.CASHIER) {
            Cashier cashier = (Cashier) client;
            detail1TextField.setText(cashier.getHonorarium().toString());
        }
    }

    @Override
    protected void onViewModeSet() {
        clientTypeComboBox.setDisable(true);
        clientTypeComboBox.setStyle("-fx-opacity: 1");
        emailTextField.setEditable(false);
        usernameTextField.setEditable(false);
        passwordField.setEditable(false);
        nameTextField.setEditable(false);
        phoneTextField.setEditable(false);
        addressTextField.setEditable(false);
        cityTextField.setEditable(false);
        detail1TextField.setEditable(false);
        detail2TextField.setEditable(false);
    }

    @Override
    protected void onAddModeSet() {
        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
        okButton.addEventFilter(ActionEvent.ACTION, this::onAddClick);
    }

    @Override
    protected void onEditMode() {
        final Button okButton = addDialogButton(getLangBundle().getString(BUTTON_APPLY_KEY), ButtonBar.ButtonData.OK_DONE);
        if (okButton == null) return;

        clientTypeComboBox.setDisable(true);
        clientTypeComboBox.setStyle("-fx-opacity: 1");

        okButton.getStylesheets().addAll("/css/buttons.css");
        okButton.getStyleClass().addAll("markAsReadBtn");
        okButton.addEventFilter(ActionEvent.ACTION, this::onEditClick);
    }
}