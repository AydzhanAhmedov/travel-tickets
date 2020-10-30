package bg.tuvarna.traveltickets.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public enum MenuContent {

    BTN_CLIENTS("label.clients", "/images/baseline_people_black_18dp.png"), // attach admin and distributor
    BTN_STATISTIC("label.statistics", "/images/baseline_insert_chart_black_18dp.png"), // statistic about cashiers attach admin, company and distributor
    BTN_NOTIFICATIONS("label.notifications", "/images/baseline_notifications_none_black_18dp.png"),// all clients
    BTN_TRAVELS("label.travels", "/images/baseline_public_black_18dp.png"), // all clients
    BTN_REQUESTS("label.requests", "/images/baseline_rule_black_18dp.png"), // company and distributor
    BTN_SOLD_TICKETS("label.sold_tickets", "/images/baseline_receipt_black_18dp.png"); // all clients

    MenuContent(String btnText, String imagePath) {
        this.btnText = btnText;
        this.imagePath = imagePath;
    }

    static private final String buttonPath = "/fxml/menu_button.fxml";

    private final String btnText;
    private final String imagePath;
    private Button button;

    public Button getButton() {
        if (button == null) {
            try {
                button = FXMLLoader.load(getClass().getResource("/fxml/menu_button.fxml"));
                button.setText(AppConfig.getLangBundle().getString(btnText));
                ((ImageView) button.getGraphic()).setImage(new Image(imagePath));
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return button;
    }
}
