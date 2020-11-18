package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.ClientTypeServiceImpl;

import javax.persistence.PostLoad;

public final class ClientEntityListener {

    @PostLoad
    public void postLoad(final Client client) {
        client.setClientType(ClientTypeServiceImpl.getInstance().findById(client.getClientType().getId()));
    }

}
