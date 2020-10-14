package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.Optional;

public interface UserRepository extends GenericCrudRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

}
