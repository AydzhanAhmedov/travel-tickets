package bg.tuvarna.traveltickets.common;

import bg.tuvarna.traveltickets.entity.ClientType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.CLIENTS_TABLE_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.REQUESTS_TABLE_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.TICKETS_TABLE_FXML_PATH;
import static bg.tuvarna.traveltickets.common.Constants.TRAVELS_TABLE_FXML_PATH;

public enum MenuContent {

    CLIENTS("label.clients", "/images/baseline_people_black_18dp.png", CLIENTS_TABLE_FXML_PATH),
    STATISTIC("label.statistics", "/images/baseline_insert_chart_black_18dp.png", null),
    TRAVELS("label.travels", "/images/baseline_public_black_18dp.png", TRAVELS_TABLE_FXML_PATH),
    REQUESTS("label.requests", "/images/baseline_rule_black_18dp.png", REQUESTS_TABLE_FXML_PATH),
    SOLD_TICKETS("label.sold_tickets", "/images/baseline_receipt_black_18dp.png", TICKETS_TABLE_FXML_PATH);

    private static final Logger LOG = LogManager.getLogger(MenuContent.class);

    MenuContent(final String btnText, final String imagePath, final String contentPath) {
        this.btnText = btnText;
        this.imagePath = imagePath;
        this.contentPath = contentPath;
    }

    private static final String buttonPath = "/fxml/menu_button.fxml";

    private final String btnText;
    private final String imagePath;
    private final String contentPath;

    private Button button;

    public Button getButton() {
        if (button == null) {
            try {
                button = FXMLLoader.load(getClass().getResource(buttonPath));
                ((ImageView) button.getGraphic()).setImage(new Image(imagePath));
            }
            catch (IOException exception) {
                LOG.error("Error loading " + this.toString().toLowerCase() + " menu button: ", exception);
            }
        }
        button.setText(AppConfig.getLangBundle().getString(btnText));

        return button;
    }

    public Parent loadContent() {
        try {
            return FXMLLoader.load(getClass().getResource(contentPath), getLangBundle());
        }
        catch (IOException e) {
            LOG.error("Error loading " + this.toString().toLowerCase() + " menu content: ", e);
            return null;
        }
    }

    private static final List<MenuContent> ADMIN_CONTENT = List.of(CLIENTS, STATISTIC);
    private static final List<MenuContent> COMPANY_CONTENT = List.of(TRAVELS, REQUESTS, SOLD_TICKETS, STATISTIC);
    private static final List<MenuContent> DISTRIBUTOR_CONTENT = List.of(CLIENTS, TRAVELS, SOLD_TICKETS, STATISTIC);
    private static final List<MenuContent> CASHIER_CONTENT = List.of(TRAVELS, SOLD_TICKETS);

    public static List<MenuContent> getAdminContent() {
        return ADMIN_CONTENT;
    }

    public static List<MenuContent> getClientContent(final ClientType.Enum clientTypeName) {
        return switch (clientTypeName) {
            case COMPANY -> COMPANY_CONTENT;
            case DISTRIBUTOR -> DISTRIBUTOR_CONTENT;
            case CASHIER -> CASHIER_CONTENT;
        };
    }

}
