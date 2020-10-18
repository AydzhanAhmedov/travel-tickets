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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class UserLoginController extends BaseController {

    private static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials!";
    private static final String ERROR_OCCURRED_MESSAGE = "An error occurred, please try again!";
    private static final String BLANK_FIELDS_MESSAGE = "Username or email and password fields must not be blank!";
    private static final String PLEASE_WAIT_MESSAGE = "Please wait..";

    private final UserService userService = UserServiceImpl.getInstance();

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Text infoField;

    @FXML
    public void onLoginButtonClicked(final MouseEvent event) {
        final String usernameOrEmail = usernameTextField.getText();
        final String password = passwordField.getText();

        if (usernameOrEmail.isBlank() || password.isBlank()) {
            infoField.setText(BLANK_FIELDS_MESSAGE);
            infoField.setFill(Color.RED);
            return;
        }

        final Task<Boolean> loginTask = JpaOperationsUtil.createTask(() -> userService.login(usernameOrEmail, password));

        loginTask.setOnFailed(this::onLoginTaskFailed);
        loginTask.setOnRunning(this::onLoginTaskRunning);
        loginTask.setOnSucceeded(e -> onLoginTaskSucceeded(loginTask.getValue()));

        new Thread(loginTask).start();
    }

    private void onLoginTaskRunning(final WorkerStateEvent event) {
        loginButton.setDisable(true);
        infoField.setText(PLEASE_WAIT_MESSAGE);
        infoField.setFill(Color.BLACK);
    }

    private void onLoginTaskSucceeded(final Boolean successfullyLoggedIn) {
        loginButton.setDisable(false/*successfullyLoggedIn*/);

        if (!successfullyLoggedIn) {
            infoField.setText(BAD_CREDENTIALS_MESSAGE);
            infoField.setFill(Color.RED);
        } else {
            infoField.setText("Success");
            infoField.setFill(Color.GREEN);
        }
    }

    private void onLoginTaskFailed(final WorkerStateEvent event) {
        loginButton.setDisable(false);
        infoField.setText(ERROR_OCCURRED_MESSAGE);
        infoField.setFill(Color.RED);
    }

}
