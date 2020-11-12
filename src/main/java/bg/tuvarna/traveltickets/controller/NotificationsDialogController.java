package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseDialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

public class NotificationsDialogController extends BaseDialogController {

    @FXML
    private Button markAllAsReadButton;

    @FXML
    private TableColumn<?, ?> columnFrom;

    @FXML
    private TableColumn<?, ?> columnMessage;

    @FXML
    private void onMarkAllAsReadClicked(final ActionEvent event) {

    }

}
