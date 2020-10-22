package bg.tuvarna.traveltickets.common;

public final class Constants {

    // Error message constants
    public static final String CANNOT_BE_INSTANTIATED_FORMAT = "%s cannot be instantiated!";
    public static final String CANNOT_BE_NULL_FORMAT = "%s cannot be null!";
    public static final String ACTION_CANNOT_BE_NULL_MESSAGE = CANNOT_BE_NULL_FORMAT.formatted("action");

    // Params
    public static final String USERNAME_OR_EMAIL_PARAM = "usernameOrEmail";

    // Bundles
    public static final String BAD_CREDENTIALS_KEY = "label.error.bad_credentials";
    public static final String UNEXPECTED_ERROR_KEY = "label.error.unexpected_error_try_again";
    public static final String BLANK_USERNAME_OR_PASSWORD_KEY = "label.error.blank_username_or_password";

    // Other constants
    public static final String EMPTY_STRING = "";
    public static final String LANG_BUNDLE_NAME = "bundles.lang_bundle";
    public static final String PERSISTENT_UNIT_NAME = "travel-ticket-persistence-unit";

    private Constants() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
