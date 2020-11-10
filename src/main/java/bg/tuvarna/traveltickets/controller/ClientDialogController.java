package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.entity.Address;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Region;
import bg.tuvarna.traveltickets.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;

public class ClientDialogController implements Initializable {

    public enum DialogMode {VIEW, ADD, EDIT}

    private Client client;

    @FXML
    private DialogPane dialogPane;

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
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> regionComboBox;

    @FXML
    private TextField detail1TextField;

    @FXML
    private TextField detail2TextField;

    @FXML
    void onChange(ActionEvent event) {
        configureDetails(clientTypeComboBox.getValue());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setDetailsInvisible();
        addCancelButton();
        clientTypeComboBox.getItems().setAll(ClientType.Enum.values());
        clientTypeComboBox.getSelectionModel().select(0);
    }

    public void initDialog(Client client, DialogMode mode){
        setMode(mode);
        if (mode != DialogMode.ADD)
            setData(client);
    }

    private void addCancelButton() {
        ButtonType cancel = new ButtonType(getLangBundle().getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().add(cancel);
    }

    private void setMode(DialogMode mode) {
        switch (mode) {
            case ADD: {
                ButtonType OK = new ButtonType(getLangBundle().getString("button.apply"), ButtonBar.ButtonData.OK_DONE);
                dialogPane.getButtonTypes().add(OK);

                Button btOk = (Button) dialogPane.lookupButton(OK);
                if (btOk == null)
                    return;

                btOk.addEventFilter(ActionEvent.ACTION, event -> {
                    readData();
                    System.out.println("data read");
                });
                break;
            }
            case EDIT: {
                ButtonType OK = new ButtonType(getLangBundle().getString("button.apply"), ButtonBar.ButtonData.OK_DONE);
                dialogPane.getButtonTypes().add(OK);
                clientTypeComboBox.setDisable(true);
                clientTypeComboBox.setStyle("-fx-opacity: 1");
                break;
            }
            case VIEW: {
                clientTypeComboBox.setDisable(true);
                clientTypeComboBox.setStyle("-fx-opacity: 1");
                emailTextField.setEditable(false);
                usernameTextField.setEditable(false);
                passwordField.setEditable(false);
                nameTextField.setEditable(false);
                phoneTextField.setEditable(false);
                addressTextField.setEditable(false);
                cityComboBox.setDisable(true);
                cityComboBox.setStyle("-fx-opacity: 1");
                regionComboBox.setDisable(true);
                regionComboBox.setStyle("-fx-opacity: 1");
                detail1TextField.setEditable(false);
                detail2TextField.setEditable(false);
                break;
            }
        }
    }

    private void setDetailsInvisible() {
        detail1Label.setVisible(false);
        detail2Label.setVisible(false);
        detail1TextField.setVisible(false);
        detail2TextField.setVisible(false);
    }

    private void configureDetails(ClientType.Enum type) {
        setDetailsInvisible();
        switch (type) {
            case CASHIER: {
                detail1Label.setText("Honorarium");
                detail1Label.setVisible(true);
                detail1TextField.setVisible(true);
                break;
            }
            case COMPANY: {
                detail1Label.setText("Image URL");
                detail1Label.setVisible(true);
                detail1TextField.setVisible(true);
                detail2Label.setText("Description");
                detail2Label.setVisible(true);
                detail2TextField.setVisible(true);
                break;
            }
        }
    }

    private void readData() {

        client.setClientType(new ClientType(clientTypeComboBox.getValue()));
        Region region = new Region(regionComboBox.getValue());
        City city = new City(cityComboBox.getValue(), region);
        Address address = new Address(city, addressTextField.getText());
        client.setAddress(address);
        client.setName(nameTextField.getText());
        client.setPhone(phoneTextField.getText());
        client.getUser().setEmail(emailTextField.getText());
        client.getUser().setUsername(emailTextField.getText());
        client.getUser().setPassword(passwordField.getText());

    }

    private void setData(Client client) {
        this.client = client;

        ClientType.Enum clientType = client.getClientType().getName();
        User user = client.getUser();

        configureDetails(clientType);
        emailTextField.setText(user.getEmail());
        usernameTextField.setText(user.getUsername());
        //passwordField
        clientTypeComboBox.getSelectionModel().select(client.getClientType().getName());
        nameTextField.setText(client.getName());
        phoneTextField.setText(client.getPhone());
        addressTextField.setText(client.getAddress().getAddress());
        cityComboBox.getSelectionModel().select(client.getAddress().getCity().getName());
        regionComboBox.getSelectionModel().select(client.getAddress().getCity().getRegion().getName());

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
}
