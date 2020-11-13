package bg.tuvarna.traveltickets.common;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppScreens.LOGIN;
import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;
import static bg.tuvarna.traveltickets.common.SupportedLanguage.ENGLISH;

/**
 * This class is responsible for all the configuration in the application.
 */
public final class AppConfig {

    private static final Logger LOG = LogManager.getLogger(AppConfig.class);

    private static SupportedLanguage language = SupportedLanguage.findByLocale(Locale.getDefault()).orElse(ENGLISH);
    private static Stage primaryStage;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(language.getLocale());

    public static ResourceBundle getLangBundle() {
        return language.getBundle();
    }

    public static SupportedLanguage getLanguage() {
        return language;
    }

    public static void setLanguage(final SupportedLanguage language) {
        AppConfig.language = language;
        DATE_TIME_FORMATTER.withLocale(language.getLocale());
        AppScreens.reloadScreens();
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStageScene(final Scene stageScene) {
        primaryStage.setScene(stageScene);
        // Center stage on screen
        final Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public static void configure(final Stage primaryStage) {
        AppConfig.primaryStage = primaryStage;
        configureHibernate();
        configurePrimaryStage();
        LOG.debug("Application configured.");
    }

    private static void configurePrimaryStage() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(e -> {
            LOG.info("Exiting the application...");
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
        new Thread(() -> {
            EntityManagerUtil.getEntityManagerFactory();
            LOG.debug("Hibernate configured.");
        }).start();
    }

    private AppConfig() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
