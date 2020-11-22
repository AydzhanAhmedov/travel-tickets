package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.common.AppScreens;
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
import bg.tuvarna.traveltickets.service.impl.SubscriberServiceImpl;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import bg.tuvarna.traveltickets.util.notifications.NotificationEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.AppConfig.setPrimaryStageScene;
import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;
import static bg.tuvarna.traveltickets.common.Constants.ACTIVE_NOTIFICATIONS_BTN_CSS;
import static bg.tuvarna.traveltickets.common.Constants.NOTIFICATIONS_BTN_CSS;
import static bg.tuvarna.traveltickets.common.Constants.NOTIFICATIONS_DIALOG_FXML_PATH;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.execute;
import static bg.tuvarna.traveltickets.util.notifications.NotificationEvent.NEW_NOTIFICATION;

public class HomeController extends BaseUndecoratedController {

    private static final Logger LOG = LogManager.getLogger(HomeController.class);

    private OffsetDateTime initialNotificationLoadTime;

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ObservableList<NotificationRecipient> notifications = FXCollections.observableArrayList();

    @FXML
    private ImageView userImageView;

    @FXML
    private Text userText;

    @FXML
    private Button notificationButton;

    @FXML
    private BorderPane childBorderPane;

    @FXML
    private VBox contentButtonsVBox;

    @FXML
    private Text contentText;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        initUserSpecificContent();
        initNotifications();
    }

    @FXML
    private void onNotificationButtonClicked(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(NOTIFICATIONS_DIALOG_FXML_PATH), AppConfig.getLangBundle());

        LOG.debug("Loading notifications dialog.");

        final DialogPane dialogPane = loader.load();
        final UndecoratedDialog<Void> dialog = new UndecoratedDialog<>(root, dialogPane);

        final Long recipientId = authService.getLoggedUser().getId();
        final OffsetDateTime lastNotificationDate = notifications.isEmpty()
                ? initialNotificationLoadTime : notifications.get(0).getNotification().getCreatedAt();

        final Task<List<NotificationRecipient>> fetchLastNotificationTask = JpaOperationsUtil
                .createTask(em -> notificationService.findAllByRecipientIdAndDateAfter(recipientId, lastNotificationDate));

        fetchLastNotificationTask.setOnSucceeded(e -> Platform.runLater(() -> {
            fetchLastNotificationTask.getValue().forEach(n -> notifications.add(0, n));
            dialog.showAndWait();
            LOG.info("New notifications fetched and added.");
        }));

        loader.<NotificationsDialogController>getController().injectNotifications(notifications, this::updateNotificationButton);
        new Thread(fetchLastNotificationTask).start();
    }

    @FXML
    private void onProfileButtonClicked(ActionEvent event) {

    }

    @FXML
    private void onLogoutButtonClicked(final ActionEvent event) {
        authService.logout();
        AppScreens.HOME.delete();
        LOG.info("User logged out.");
        setPrimaryStageScene(LOGIN.getScene());
    }

    private void fireNewNotificationEvent() {
        notificationButton.fireEvent(new NotificationEvent());
        LOG.debug("New notification event fired.");
    }

    private void updateNotificationButton() {
        updateNotificationButton(notifications.isEmpty() || notifications.stream().allMatch(notificationService::isSeen));
    }

    private void updateNotificationButton(final boolean allSeen) {
        notificationButton.getStyleClass().clear();
        notificationButton.getStyleClass().add(allSeen ? NOTIFICATIONS_BTN_CSS : ACTIVE_NOTIFICATIONS_BTN_CSS);
        LOG.debug("Notification bell updated to {}.", allSeen ? "inactive" : "active");
    }

    private void initUserSpecificContent() {
        LOG.info("Initializing user specific content.");

        (authService.loggedUserIsAdmin()
                ? MenuContent.getAdminContent()
                : MenuContent.getClientContent(authService.getLoggedClientTypeName())
        ).forEach(c -> {
            final Button button = c.getButton();
            button.setOnAction(e -> {
                contentText.setText(button.getText());
                childBorderPane.setCenter(c.loadContent());
            });
            contentButtonsVBox.getChildren().add(contentButtonsVBox.getChildren().size() - 1, button);
        });
        ((Button) contentButtonsVBox.getChildren().get(1)).fire();

        if (authService.loggedUserIsAdmin()) return;

        switch (authService.getLoggedClientTypeName()) {
            case DISTRIBUTOR -> {
                userImageView.setImage(new Image("images/logo_distributor.png"));
                userText.setText(getLangBundle().getString("label.distributor"));
            }
            case CASHIER -> {
                userImageView.setImage(new Image("images/logo_cashier.png"));
                userText.setText(getLangBundle().getString("label.cashier"));
            }
            case COMPANY -> {
                Company company = (Company) authService.getLoggedClient();
                try {
                    userImageView.setImage(new Image(company.getLogoUrl()));
                }
                catch (IllegalArgumentException e) {
                    LOG.warn("Invalid logo url for logged company: ", e);
                    userImageView.setImage(new Image("images/logo_cashier.png"));
                }
                userText.setText(company.getName());
            }
        }
    }

    private void initNotifications() {
        LOG.info("Initializing user's notifications.");

        if (authService.loggedUserIsAdmin()) {
            LOG.debug("Disabling notifications for logged admin.");

            notificationButton.setVisible(false);
            notificationButton.setManaged(false);
            notificationButton.setDisable(true);

            return;
        }

        LOG.debug("Loading user's notifications.");

        final List<NotificationRecipient> notifications = execute(em -> notificationService.findAllByRecipientId(authService.getLoggedUser().getId()));
        this.notifications.addAll(notifications);
        initialNotificationLoadTime = OffsetDateTime.now();
        notificationButton.addEventHandler(NEW_NOTIFICATION, e -> updateNotificationButton(false));

        updateNotificationButton();

        SubscriberServiceImpl.getInstance().subscribeForNewTravels(n -> fireNewNotificationEvent());
        SubscriberServiceImpl.getInstance().subscribeForDistributorSpecificTravels(n -> fireNewNotificationEvent());

        if (authService.getLoggedClientTypeName() == ClientType.Enum.CASHIER) {
            LOG.debug("Disabling scheduled notifications for logged cashier.");
            return;
        }

//        scheduledExecutorService.scheduleWithFixedDelay(
//                new ScheduledTicketsNotificationCheck(n -> fireNewNotificationEvent()),
//                0L, AppConfig.getNotificationCheckPeriod(), TimeUnit.MILLISECONDS
//        );
    }

}
