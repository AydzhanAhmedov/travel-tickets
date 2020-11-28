package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.User;

public interface AuthService {

    User getLoggedUser();

    Client getLoggedClient();

    ClientType.Enum getLoggedClientTypeName();

    boolean loggedUserIsAdmin();

    User login(String usernameOrEmail, String password);

    User findByUsername(String username);

    User findByEmail(String email);

    void logout();

}
