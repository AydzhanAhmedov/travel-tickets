package bg.tuvarna.traveltickets.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Arrays;

/**
 * This enumeration holds information about the screens in the application. It provides getter method for screen's
 * scene and takes care for reloading the fxml files on language change.
 */
public enum AppScreens {
    LOGIN("/fxml/login.fxml"),
    HOME("/fxml/home.fxml");

    private final String fxmlPath;

    private Parent parent;
    private Scene scene;

    AppScreens(final String fxmlPath) {
        this.fxmlPath = fxmlPath;
        reload();
    }

    public Scene getScene() {
        return scene;
    }

    static void reloadScreens() {
        Arrays.stream(values()).forEach(AppScreens::reload);
    }

    void reload() {
        try {
            final Parent parent = new FXMLLoader(getClass().getResource(fxmlPath), AppConfig.getLangBundle()).load();
            if (scene == null) {
                this.parent = parent;
                scene = new Scene(parent);
            } else {
                this.parent.getScene().setRoot(parent);
                this.parent = parent;
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
