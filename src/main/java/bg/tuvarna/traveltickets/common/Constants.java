package bg.tuvarna.traveltickets.common;

public final class Constants {

    // Error message constants
    public static final String CANNOT_BE_INSTANTIATED_FORMAT = "%s cannot be instantiated!";
    public static final String CANNOT_BE_NULL_FORMAT = "%s cannot be null!";
    public static final String ACTION_CANNOT_BE_NULL_MESSAGE = CANNOT_BE_NULL_FORMAT.formatted("action");

    // Other constants
    public static final String PERSISTENT_UNIT_NAME = "travel-ticket-persistence-unit";

    public static final String USERNAME_OR_EMAIL_PARAM = "usernameOrEmail";

    private Constants() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
