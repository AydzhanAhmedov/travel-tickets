package bg.tuvarna.traveltickets;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    public static void main(final String... args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        App.primaryStage = primaryStage;

        configurePrimaryStage();

        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/bg.tuvarna.traveltickets.controller/login.fxml"))));
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void configurePrimaryStage() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(e -> {
            EntityManagerUtil.closeEntityManagerFactory();
            Platform.exit();
            System.exit(0);
        });
    }

}