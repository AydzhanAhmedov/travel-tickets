package bg.tuvarna.traveltickets.control;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;

public class ConfirmDialog extends Alert {

    public ConfirmDialog(final String caption, final String contentText) {
        super(AlertType.CONFIRMATION);

        this.setHeaderText(caption);
        this.setContentText(contentText);
        init();

    }

    private void init() {
        this.initStyle(StageStyle.UNDECORATED);

        ButtonType btnYes = new ButtonType(getLangBundle().getString("label.button.yes"), ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType(getLangBundle().getString("label.button.no"), ButtonBar.ButtonData.NO);
        this.getButtonTypes().setAll(btnYes, btnNo);
    }


}
