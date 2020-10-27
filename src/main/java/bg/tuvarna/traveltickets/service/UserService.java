package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.User;

public interface UserService {

    User getLoggedUser();

    boolean loggedUserIsAdmin();

    User login(String usernameOrEmail, String password);

    void logout();

}
