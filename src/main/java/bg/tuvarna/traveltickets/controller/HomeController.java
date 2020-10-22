package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;

public class HomeController extends BaseController {

    @FXML
    private Button notificationButton;

    @FXML
    private void onLogoutButtonClicked(final MouseEvent event) {
        getPrimaryStage().setScene(LOGIN.getScene());
    }

    @FXML
    private void onNotificationButtonClicked(final MouseEvent event) {

    }

}
