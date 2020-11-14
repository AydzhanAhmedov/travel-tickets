package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedController;
import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.NotificationServiceImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.HOURS_KEY;
import static bg.tuvarna.traveltickets.common.Constants.MINUTES_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SECONDS_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SEEN_BUTTON_KEY;
import static bg.tuvarna.traveltickets.common.Constants.SYSTEM_KEY;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;

public class NotificationsDialogController extends BaseUndecoratedController {

    @FunctionalInterface
    public interface NotificationBellUpdater {
        void update();
    }

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    private NotificationBellUpdater notificationBellUpdater;

    @FXML
    private Button markAllAsSeenButton;

    @FXML
    private TableView<NotificationRecipient> tableNotifications;

    @FXML
    private TableColumn<NotificationRecipient, Notification> columnFrom;

    @FXML
    private TableColumn<NotificationRecipient, Notification> columnMessage;

    @FXML
    private TableColumn<NotificationRecipient, Notification> columnDate;

    @FXML
    private TableColumn<NotificationRecipient, NotificationRecipient> columnAction;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        initColumns();
    }

    @FXML
    private void onMarkAllAsSeenClicked(final ActionEvent event) {
        executeInTransaction(em -> notificationService.markAsSeen(tableNotifications.getItems()));
        tableNotifications.refresh();
        notificationBellUpdater.update();
    }

    public void injectNotifications(final ObservableList<NotificationRecipient> notifications,
                                    final NotificationBellUpdater notificationBellUpdater) {

        tableNotifications.setItems(notifications);
        this.notificationBellUpdater = notificationBellUpdater;
    }

    private void initColumns() {
        columnFrom.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Notification item, final boolean empty) {
                super.updateItem(item, empty);

                if (!empty && notificationService.isSeen(getTableView().getItems().get(getIndex()))) onSeen(this);

                final String from = empty ? null : item.getCreatedBy().getUsername();
                setText(authService.getLoggedUser().getUsername().equals(from) ? getLangBundle().getString(SYSTEM_KEY) : from);
            }
        });
        columnMessage.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Notification item, final boolean empty) {
                super.updateItem(item, empty);
                if (!empty && notificationService.isSeen(getTableView().getItems().get(getIndex()))) onSeen(this);
                setText(empty ? null : item.getMessage());
            }
        });
        columnDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(final Notification item, final boolean empty) {
                super.updateItem(item, empty);
                if (!empty && notificationService.isSeen(getTableView().getItems().get(getIndex()))) onSeen(this);

                String date = empty ? null : item.getCreatedAt().format(AppConfig.getDateTimeFormatter());
                if (!empty) {
                    final Instant currInstant = OffsetDateTime.now().toInstant();
                    final Instant createdAtInstant = item.getCreatedAt().toInstant();

                    final Duration duration = Duration.between(createdAtInstant, currInstant);
                    if (duration.toDays() == 0) {
                        if (duration.toHours() > 0)
                            date = getLangBundle().getString(HOURS_KEY).formatted(duration.toHours());
                        else if (duration.toMinutes() > 0)
                            date = getLangBundle().getString(MINUTES_KEY).formatted(duration.toMinutes());
                        else
                            date = getLangBundle().getString(SECONDS_KEY).formatted(duration.toSeconds());
                    }
                }
                setText(date);
            }
        });
        columnAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button(getLangBundle().getString(SEEN_BUTTON_KEY));

            @Override
            public void updateItem(NotificationRecipient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    final NotificationRecipient notification = getTableView().getItems().get(getIndex());

                    btn.getStylesheets().addAll(markAllAsSeenButton.getStylesheets());
                    btn.getStyleClass().addAll(markAllAsSeenButton.getStyleClass());
                    btn.setOnAction(event -> {
                        executeInTransaction(em -> notificationService.markAsSeen(notification));
                        getTableView().getItems().set(getIndex(), notification);
                        notificationBellUpdater.update();
                    });

                    if (notificationService.isSeen(notification)) btn.setDisable(true);

                    setGraphic(btn);
                }
            }
        });

        columnFrom.setCellValueFactory(new PropertyValueFactory<>("notification"));
        columnMessage.setCellValueFactory(new PropertyValueFactory<>("notification"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("notification"));
    }

    private void onSeen(final TableCell<?, ?> cell) {
        cell.setOpacity(0.5);
    }

}
