package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.common.SupportedLanguage;
import bg.tuvarna.traveltickets.controller.base.BaseController;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;
import static bg.tuvarna.traveltickets.common.AppScreens.HOME;
import static bg.tuvarna.traveltickets.common.Constants.BAD_CREDENTIALS_KEY;
import static bg.tuvarna.traveltickets.common.Constants.BLANK_USERNAME_OR_PASSWORD_KEY;
import static bg.tuvarna.traveltickets.common.Constants.EMPTY_STRING;
import static bg.tuvarna.traveltickets.common.Constants.UNEXPECTED_ERROR_KEY;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.createTransactionTask;

public class UserLoginController extends BaseController {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Text errorText;

    @FXML
    private ImageView errorImageView;

    @FXML
    private ChoiceBox<SupportedLanguage> languageChoiceBox;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        languageChoiceBox.setValue(AppConfig.getLanguage());
        languageChoiceBox.getItems().addAll(SupportedLanguage.values());
        languageChoiceBox.setOnAction(e -> AppConfig.setLanguage(languageChoiceBox.getValue()));
    }

    @FXML
    private void onLoginButtonClicked(final Event event) {
        final String usernameOrEmail = usernameTextField.getText();
        final String password = passwordField.getText();

        if (usernameOrEmail.isBlank() || password.isBlank()) {
            setErrorText(getLangBundle().getString(BLANK_USERNAME_OR_PASSWORD_KEY));
            return;
        }

        final Task<Boolean> loginTask = createTransactionTask(() -> authService.login(usernameOrEmail, password) != null);

        loginTask.setOnFailed(this::onLoginTaskFailed);
        loginTask.setOnRunning(this::onLoginTaskRunning);
        loginTask.setOnSucceeded(e -> onLoginTaskSucceeded(loginTask.getValue()));

        new Thread(loginTask).start();
    }

    private void onLoginTaskRunning(final WorkerStateEvent event) {
        setDisableOnButtonAndTextFields(true);
    }

    private void onLoginTaskSucceeded(final Boolean successfullyLoggedIn) {
        setDisableOnButtonAndTextFields(false);

        if (successfullyLoggedIn) {
            clearTextFields();
            getPrimaryStage().setScene(HOME.getScene());
        } else setErrorText(getLangBundle().getString(BAD_CREDENTIALS_KEY));
    }

    private void onLoginTaskFailed(final WorkerStateEvent event) {
        setDisableOnButtonAndTextFields(false);
        setErrorText(getLangBundle().getString(UNEXPECTED_ERROR_KEY));
    }

    private void clearTextFields() {
        setErrorText(EMPTY_STRING);
        usernameTextField.setText(EMPTY_STRING);
        passwordField.setText(EMPTY_STRING);
    }

    private void setErrorText(final String text) {
        errorImageView.setVisible(!text.isBlank());
        errorText.setText(text);
    }

    private void setDisableOnButtonAndTextFields(final boolean value) {
        usernameTextField.setDisable(value);
        passwordField.setDisable(value);
        loginButton.setDisable(value);
    }

    public void onKeyPressed(final KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)
        onLoginButtonClicked(keyEvent);
    }
}
