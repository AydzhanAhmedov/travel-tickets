package bg.tuvarna.traveltickets.common;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.persistence.EntityManagerFactory;
import java.util.Locale;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;
import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;
import static bg.tuvarna.traveltickets.common.SupportedLanguage.ENGLISH;

/**
 * This class is responsible for all the configuration in the application.
 */
public final class AppConfig {

    private static SupportedLanguage language = SupportedLanguage.findByLocale(Locale.getDefault()).orElse(ENGLISH);
    private static Stage primaryStage;

    public static ResourceBundle getLangBundle() {
        return language.getBundle();
    }

    public static SupportedLanguage getLanguage() {
        return language;
    }

    public static void setLanguage(final SupportedLanguage language) {
        AppConfig.language = language;
        AppScreens.reloadScreens();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void configure(final Stage primaryStage) {
        AppConfig.primaryStage = primaryStage;
        configureHibernate();
        configurePrimaryStage();
    }

    private static void configurePrimaryStage() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(e -> {
            EntityManagerUtil.closeEntityManagerFactory();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setScene(LOGIN.getScene());
        primaryStage.show();
    }

    /**
     * This method calls {@link EntityManagerUtil#getEntityManagerFactory()} which triggers static initialization
     * of {@link EntityManagerFactory}, this is done on a new thread to prevent ui from waiting.
     */
    @SuppressWarnings("all")
    private static void configureHibernate() {
        new Thread(EntityManagerUtil::getEntityManagerFactory).start();
    }

    private AppConfig() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
