package bg.tuvarna.traveltickets.common;

public final class Constants {

    // Error message constants
    public static final String CANNOT_BE_INSTANTIATED_FORMAT = "%s cannot be instantiated!";

    // Other constants
    public static final String PERSISTENT_UNIT_NAME = "travel-ticket-persistence-unit";

    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";

    private Constants() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
