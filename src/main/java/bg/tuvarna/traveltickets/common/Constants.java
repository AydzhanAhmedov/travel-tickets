package bg.tuvarna.traveltickets.common;

public final class Constants {

    // Error message constants
    public static final String CANNOT_BE_INSTANTIATED_FORMAT = "%s cannot be instantiated!";
    public static final String CANNOT_BE_NULL_FORMAT = "%s cannot be null!";
    public static final String ACTION_CANNOT_BE_NULL_MESSAGE = CANNOT_BE_NULL_FORMAT.formatted("action");
    public static final String CLIENT_NOT_FOUND_FORMAT = "client with id %d not found in database!";
    public static final String CANNOT_BE_EMPTY_FORMAT = "%s cannot be empty!";
    public static final String RECIPIENT_LIST_CANNOT_BE_EMPTY = CANNOT_BE_EMPTY_FORMAT.formatted("recipient ids list");

    // Params
    public static final String USERNAME_OR_EMAIL_PARAM = "usernameOrEmail";
    public static final String USERNAME_PARAM = "username";
    public static final String EMAIL_PARAM = "email";
    public static final String USER_ID_PARAM = "userId";
    public static final String CITY_NAME_PARAM = "cityName";
    public static final String TRAVEL_STATUS_ID_PARAM = "travelStatusId";
    public static final String REQUEST_STATUS_ID_PARAM = "requestStatusId";
    public static final String CLIENT_TYPE_ID_PARAM = "clientTypeId";
    public static final String TRAVEL_ID_PARAM = "travelId";
    public static final String DATE_PARAM = "date";

    // Bundles
    public static final String ACCEPT_BUTTON_KEY = "label.button.accept";
    public static final String DECLINE_BUTTON_KEY = "label.button.decline";

    public static final String SEEN_BUTTON_KEY = "label.button.seen";
    public static final String EDIT_BUTTON_KEY = "label.button.edit";
    public static final String REQUEST_BUTTON_KEY = "label.button.request";
    public static final String SELL_BUTTON_KEY = "label.button.sell";

    public static final String SYSTEM_KEY = "label.system";
    public static final String HOURS_KEY = "label.short_hours_ago";
    public static final String MINUTES_KEY = "label.short_minutes_ago";
    public static final String SECONDS_KEY = "label.short_seconds_ago";
    public static final String HONORARIUM_KEY = "label.honorarium";
    public static final String IMAGE_URL_KEY = "label.image_url";
    public static final String DESCRIPTION_KEY = "label.description";

    public static final String BAD_CREDENTIALS_KEY = "label.error.bad_credentials";
    public static final String UNEXPECTED_ERROR_KEY = "label.error.unexpected_error_try_again";
    public static final String BLANK_USERNAME_OR_PASSWORD_KEY = "label.error.blank_username_or_password";
    public static final String INVALID_EMAIL_KEY = "label.error.invalid_email";
    public static final String INVALID_USERNAME_KEY = "label.error.invalid_username";
    public static final String USERNAME_USED_KEY = "label.error.username_used";
    public static final String EMAIL_USED_KEY = "label.error.email_used";
    public static final String INVALID_PASSWORD_KEY = "label.error.invalid_password";
    public static final String BLANK_NAME_KEY = "label.error.blank_name";
    public static final String INVALID_PHONE_KEY = "label.error.invalid_phone";
    public static final String BLANK_CITY_KEY = "label.error.blank_city";
    public static final String INVALID_HONORARIUM_KEY = "label.error.invalid_honorarium";
    public static final String BLANK_URL_KEY = "label.error.blank_url";
    public static final String BLANK_DESCRIPTION_KEY = "label.error.blank_description";

    public static final String BUTTON_APPLY_KEY = "label.button.apply";

    // FXML
    public static final String CLIENT_DIALOG_FXML_PATH = "/fxml/client_dialog.fxml";
    public static final String TRAVEL_DIALOG_FXML_PATH = "/fxml/travel_dialog.fxml";
    public static final String TICKET_DIALOG_FXML_PATH = "/fxml/ticket_dialog.fxml";
    public static final String ROUTE_VIEW_FXML_PATH = "/fxml/route_view.fxml";
    public static final String NOTIFICATIONS_DIALOG_FXML_PATH = "/fxml/notifications_dialog.fxml";
    public static final String CLIENTS_TABLE_FXML_PATH = "/fxml/table_clients.fxml";
    public static final String TRAVELS_TABLE_FXML_PATH = "/fxml/table_travels.fxml";
    public static final String REQUESTS_TABLE_FXML_PATH = "/fxml/table_requests.fxml";
    public static final String TICKETS_TABLE_FXML_PATH = "/fxml/table_tickets.fxml";

    // Other constants
    public static final String EMPTY_STRING = "";
    public static final String LANG_BUNDLE_NAME = "bundles.lang_bundle";
    public static final String PERSISTENT_UNIT_NAME = "travel-ticket-persistence-unit";
    public static final String ABLY_API_KEY = "WF2iBA.9Sd_xQ:7y8STCyKVAhHHto0";
    public static final String ACTIVE_NOTIFICATIONS_BTN_CSS = "activeNotificationsButton";
    public static final String NOTIFICATIONS_BTN_CSS = "notificationsButton";

    public static final Long DEFAULT_NOTIFICATION_CHECK_PERIOD_MILLS = 60L * 60L * 60L * 1000L;
    public static final String NEW_TRAVELS_CHANNEL = "new-travels";
    public static final String DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT = "distributor-%s";

    private Constants() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
