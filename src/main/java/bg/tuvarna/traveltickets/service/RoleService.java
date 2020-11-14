package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Role findByName(Role.Enum roleName);

}
