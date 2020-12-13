package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.util.notifications.LoggedRecipientNotifier;
import io.ably.lib.types.Message;

public interface SubscriberService {

    void subscribeForNewTravels(LoggedRecipientNotifier<Message> notifier);

    void subscribeForDistributorSpecificTravels(LoggedRecipientNotifier<Message> notifier);

}
