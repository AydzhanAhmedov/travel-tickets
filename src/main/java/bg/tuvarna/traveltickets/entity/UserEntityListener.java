package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.RoleServiceImpl;

import javax.persistence.PostLoad;

public final class UserEntityListener {

    @PostLoad
    public void postLoad(final User user) {
        user.setRole(RoleServiceImpl.getInstance().findById(user.getRole().getId()));
    }

}
