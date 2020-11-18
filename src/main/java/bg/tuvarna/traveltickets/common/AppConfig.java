package bg.tuvarna.traveltickets.common;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.ConnectionState;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ClientOptions;
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
import static bg.tuvarna.traveltickets.common.Constants.ABLY_API_KEY;
import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.DEFAULT_NOTIFICATION_CHECK_PERIOD_MILLS;
import static bg.tuvarna.traveltickets.common.SupportedLanguage.ENGLISH;

/**
 * This class is responsible for all the configuration in the application.
 */
public final class AppConfig {

    private static final Logger LOG = LogManager.getLogger(AppConfig.class);

    private static SupportedLanguage language = SupportedLanguage.findByLocale(Locale.getDefault()).orElse(ENGLISH);
    private static Stage primaryStage;
    private static Long notificationCheckPeriod = DEFAULT_NOTIFICATION_CHECK_PERIOD_MILLS;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(language.getLocale());
    private static DateTimeFormatter shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(language.getLocale());

    private static AblyRealtime ablyClient;
    private static boolean ablyIsEnabled;

    public static AblyRealtime getAblyClient() {
        return ablyClient;
    }

    public static ResourceBundle getLangBundle() {
        return language.getBundle();
    }

    public static boolean ablyIsEnabled() {
        return ablyIsEnabled;
    }

    public static void setAblyIsEnabled(final boolean ablyIsEnabled) {
        AppConfig.ablyIsEnabled = ablyIsEnabled;
    }

    public static SupportedLanguage getLanguage() {
        return language;
    }

    public static void setLanguage(final SupportedLanguage language) {
        AppConfig.language = language;
        dateTimeFormatter = dateTimeFormatter.withLocale(language.getLocale());
        shortDateTimeFormatter = shortDateTimeFormatter.withLocale(language.getLocale());
        AppScreens.reloadScreens();
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static DateTimeFormatter getShortDateTimeFormatter() {
        return shortDateTimeFormatter;
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

    public static Long getNotificationCheckPeriod() {
        return notificationCheckPeriod;
    }

    public static void setNotificationCheckPeriod(final Long notificationCheckPeriod) {
        AppConfig.notificationCheckPeriod = notificationCheckPeriod;
    }

    public static void configure(final Stage primaryStage) {
        AppConfig.primaryStage = primaryStage;
        //configureAbly();
        configureHibernate();
        configurePrimaryStage();
        LOG.debug("Application configured.");
    }

    private static void configurePrimaryStage() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(e -> {
            LOG.warn("Exiting the application...");
            try {
                Platform.exit();
                EntityManagerUtil.closeEntityManagerFactory();
                // ablyClient.close();
            }
            catch (Exception ex) {
                LOG.error("Error on closing app: ", ex);
            }
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

    private static void configureAbly() {
        try {
            ablyClient = new AblyRealtime(new ClientOptions(ABLY_API_KEY));
            ablyIsEnabled = true;

            ablyClient.connection.on(ConnectionState.connected, state -> {
                switch (state.current) {
                    case connected -> LOG.debug("Successfully connected to ably.");
                    case disconnected -> LOG.error("Ably client is disconnected.");
                    case closed -> LOG.debug("Ably client closed.");
                    case failed -> LOG.error("Error connecting to ably.");
                }
            });

            LOG.debug("Ably configured.");
        }
        catch (AblyException e) {
            LOG.error("Error configuring Ably: ", e);
        }
    }

    private AppConfig() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
