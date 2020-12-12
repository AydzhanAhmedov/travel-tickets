package bg.tuvarna.traveltickets;

import bg.tuvarna.traveltickets.common.AppConfig;
import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {

    public static void main(final String... args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        AppConfig.configure(primaryStage);
    }

}