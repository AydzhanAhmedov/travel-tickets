package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.common.MenuContent;
import bg.tuvarna.traveltickets.control.UndecoratedDialog;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedController;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.NotificationServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.setPrimaryStageScene;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;
import static bg.tuvarna.traveltickets.common.Constants.ACTIVE_NOTIFICATIONS_BTN_CSS;
import static bg.tuvarna.traveltickets.common.Constants.NOTIFICATIONS_BTN_CSS;
import static bg.tuvarna.traveltickets.common.Constants.NOTIFICATIONS_DIALOG_FXML_PATH;

public class HomeController extends BaseUndecoratedController {

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    private final ObservableList<NotificationRecipient> notifications = FXCollections.observableArrayList();

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

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        authService.getLoggedUserMenuContent().forEach(c -> {
            Button button = c.getButton();
            button.setOnMouseClicked(getEventHandler(c));
            leftVBox.getChildren().add(leftVBox.getChildren().size() - 1, button);
        });
        notifications.addAll(notificationService.findAllByRecipientId(authService.getLoggedUser().getId()));

        updateNotificationButton();
        initUserSpecificView();
    }

    @FXML
    private void onLogoutButtonClicked(final MouseEvent event) {
        authService.logout();
        setPrimaryStageScene(LOGIN.getScene());
    }

    @FXML
    private void onNotificationButtonClicked(final MouseEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(NOTIFICATIONS_DIALOG_FXML_PATH), AppConfig.getLangBundle());

        final DialogPane dialogPane = loader.load();
        final UndecoratedDialog<Void> dialog = new UndecoratedDialog<>(root, dialogPane);

        loader.<NotificationsDialogController>getController().injectNotifications(notifications, this::updateNotificationButton);

        dialog.showAndWait();
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

    private void updateNotificationButton() {
        final boolean noNewNotifications = notifications.isEmpty() || notifications.stream().allMatch(notificationService::isSeen);
        notificationButton.getStyleClass().clear();
        notificationButton.getStyleClass().add(noNewNotifications ? NOTIFICATIONS_BTN_CSS : ACTIVE_NOTIFICATIONS_BTN_CSS);
    }

}
