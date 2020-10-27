package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseController;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;

public class HomeController extends BaseController {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private Button notificationButton;

    @FXML
    private BorderPane childBorderPane;

    @FXML
    private void onLogoutButtonClicked(final MouseEvent event) {
        authService.logout();
        getPrimaryStage().setScene(LOGIN.getScene());
    }

    @FXML
    private void onNotificationButtonClicked(final MouseEvent event) {

    }

}
