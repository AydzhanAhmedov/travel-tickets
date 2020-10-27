package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

public interface UserRepository extends GenericCrudRepository<User, Long> {

    User findByUsernameOrEmail(String usernameOrEmail);

}
