package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedController;
import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.service.NotificationService;
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
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.SEEN_BUTTON_KEY;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;

public class NotificationsDialogController extends BaseUndecoratedController {

    @FunctionalInterface
    public interface NotificationBellUpdater {
        void update();
    }

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
                setText(empty ? null : item.getCreatedBy().getUsername());
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
                setText(empty ? null : item.getCreatedAt().format(AppConfig.getDateTimeFormatter()));
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
