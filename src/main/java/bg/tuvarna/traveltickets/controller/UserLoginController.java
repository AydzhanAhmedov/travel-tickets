package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseController;
import bg.tuvarna.traveltickets.service.UserService;
import bg.tuvarna.traveltickets.service.impl.UserServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class UserLoginController extends BaseController {

    private static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials!";
    private static final String ERROR_OCCURRED_MESSAGE = "An error occurred, please try again!";
    private static final String BLANK_FIELDS_MESSAGE = "Username and password fields must not be blank!";

    private final UserService userService = UserServiceImpl.getInstance();

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
    private void onLoginButtonClicked(final MouseEvent event) {
        final String usernameOrEmail = usernameTextField.getText();
        final String password = passwordField.getText();

        if (usernameOrEmail.isBlank() || password.isBlank()) {
            setErrorText(BLANK_FIELDS_MESSAGE);
            return;
        }

        final Task<Boolean> loginTask = JpaOperationsUtil.createTask(() -> userService.login(usernameOrEmail, password));

        loginTask.setOnFailed(this::onLoginTaskFailed);
        loginTask.setOnRunning(this::onLoginTaskRunning);
        loginTask.setOnSucceeded(e -> onLoginTaskSucceeded(loginTask.getValue()));

        new Thread(loginTask).start();
    }

    private void onLoginTaskRunning(final WorkerStateEvent event) {
        setDisableOnButtonAndTextFields(true);
    }

    private void onLoginTaskSucceeded(final Boolean successfullyLoggedIn) {
        setDisableOnButtonAndTextFields(successfullyLoggedIn);
        if (!successfullyLoggedIn) {
            setErrorText(BAD_CREDENTIALS_MESSAGE);
        }
    }

    private void onLoginTaskFailed(final WorkerStateEvent event) {
        setDisableOnButtonAndTextFields(false);
        setErrorText(ERROR_OCCURRED_MESSAGE);
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

}
