package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.common.MenuContent;
import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.controller.base.BaseController;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.setPrimaryStageScene;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;
import static bg.tuvarna.traveltickets.common.Constants.NOTIFICATIONS_DIALOG_FXML_PATH;

public class HomeController extends BaseController {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private ImageView userImageView;

    @FXML
    private Text userText;

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
        setPrimaryStageScene(LOGIN.getScene());
    }

    @FXML
    private void onNotificationButtonClicked(final MouseEvent event) throws IOException {
        final DialogPane dialogPane = new FXMLLoader(getClass().getResource(NOTIFICATIONS_DIALOG_FXML_PATH), AppConfig.getLangBundle()).load();
        final Dialog<Void> dialog = new UndecoratedDialog<>(root, dialogPane);

        dialog.showAndWait();
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

        initUserSpecificView();
    }

    private void initUserSpecificView() {
        if (authService.loggedUserIsAdmin())
            return;

        ClientType.Enum clientType = authService.getLoggedClientTypeName();

        switch (clientType) {
            case DISTRIBUTOR -> {
                Image image = new Image("images/logo_distributor.png");
                userImageView.setImage(image);
                userText.setText(getLangBundle().getString("label.distributor"));
            }
            case CASHIER -> {
                Image image = new Image("images/logo_cashier.png");
                userImageView.setImage(image);
                userText.setText(getLangBundle().getString("label.cashier"));
            }
            case COMPANY -> {
                Company company = (Company) AuthServiceImpl.getInstance().getLoggedClient();

                // check if image is laoded
                Image image = new Image(company.getLogoUrl());
                userImageView.setImage(image);
                userText.setText(company.getName());
            }
        }
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
                    getLangBundle());
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
