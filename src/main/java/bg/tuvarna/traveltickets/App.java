package bg.tuvarna.traveltickets;

import bg.tuvarna.traveltickets.common.AppConfig;
import javafx.application.Application;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public final class App extends Application {

    public static void main(final String... args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {

        System.out.println(BCrypt.hashpw("cashier1", BCrypt.gensalt()));
        System.out.println(BCrypt.hashpw("company1", BCrypt.gensalt()));
        System.out.println(BCrypt.hashpw("distributor1", BCrypt.gensalt()));
        AppConfig.configure(primaryStage);
    }

}