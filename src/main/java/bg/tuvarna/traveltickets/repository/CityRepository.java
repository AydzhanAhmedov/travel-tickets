package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

public interface CityRepository extends GenericCrudRepository<City, Long> {

    City findByName(String cityName);
}
