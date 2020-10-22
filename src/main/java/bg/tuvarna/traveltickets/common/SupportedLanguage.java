package bg.tuvarna.traveltickets.common;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.Constants.LANG_BUNDLE_NAME;

/**
 * This enumeration holds information about the supported languages in the application.
 */
public enum SupportedLanguage {
    ENGLISH(Locale.ENGLISH),
    BULGARIAN(new Locale("bg", "BG"));

    private final Locale locale;
    private final ResourceBundle langBundle;

    SupportedLanguage(final Locale locale) {
        this.locale = locale;
        langBundle = ResourceBundle.getBundle(LANG_BUNDLE_NAME, locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public ResourceBundle getBundle() {
        return langBundle;
    }

    public static Optional<SupportedLanguage> findByLocale(final Locale locale) {
        return Arrays.stream(values()).filter(lang -> lang.getLocale().equals(locale)).findFirst();
    }

    @Override
    public String toString() {
        return locale.getLanguage().toUpperCase();
    }

}
