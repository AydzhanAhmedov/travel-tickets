package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.common.MenuContent;
import bg.tuvarna.traveltickets.controller.base.BaseController;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;

public class HomeController extends BaseController {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private Button logoutButton;

    @FXML
    private Button notificationButton;

    @FXML
    private BorderPane childBorderPane;

    @FXML
    private VBox leftVBox;

    @FXML
    private void onLogoutButtonClicked(final MouseEvent event) {
        authService.logout();
        getPrimaryStage().setScene(LOGIN.getScene());
    }

    @FXML
    private void onNotificationButtonClicked(final MouseEvent event) {

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        EnumSet<MenuContent> menuContents = authService.getLoggedUserMenuContent();

        menuContents.forEach(c -> {
            Button button = c.getButton();
            button.setOnMouseClicked(getEventHandler(c));
            leftVBox.getChildren().add(leftVBox.getChildren().size() - 1, button);
        });
    }

    private EventHandler<MouseEvent> getEventHandler(MenuContent content) {
        return switch (content) {
            case BTN_CLIENTS -> event -> {
                btnClients();
            };
            case BTN_NOTIFICATIONS -> event -> {
            };
            case BTN_TRAVELS -> event -> {
            };
            case BTN_REQUESTS -> event -> {
            };
            case BTN_STATISTIC -> event -> {
            };
            case BTN_SOLD_TICKETS -> event -> {
            };
        };
    }

    public void btnClients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/table_clients.fxml"),
                    AppConfig.getLangBundle());
            BorderPane borderPane = loader.load();
            ClientsTableController controller = loader.getController();
            // Use controller to set data
            childBorderPane.setCenter(borderPane);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
