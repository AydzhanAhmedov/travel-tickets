package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.common.MenuContent;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.User;

import java.util.List;

public interface AuthService {

    User getLoggedUser();

    Client getLoggedClient();

    ClientType.Enum getLoggedClientTypeName();

    boolean loggedUserIsAdmin();

    User login(String usernameOrEmail, String password);

    void logout();

    List<MenuContent> getLoggedUserMenuContent();

}
