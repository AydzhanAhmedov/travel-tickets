package bg.tuvarna.traveltickets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(final String... args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/main.fxml"))));
        primaryStage.show();
    }

}
