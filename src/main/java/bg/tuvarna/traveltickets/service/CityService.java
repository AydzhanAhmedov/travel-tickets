package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.City;

public interface CityService {

    City findOrAddByName(String name);
}
