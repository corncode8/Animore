package umc.animore.controller;

import umc.animore.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.animore.repository.LocationRepository;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }




    @PostMapping
    public Location saveLocation(@RequestBody Location location) {
        return locationRepository.save(location);
    }


    @GetMapping("/{locationId}")
    public Location getLocation(@PathVariable int locationId) {
        return locationRepository.findByLocationId(locationId);
    }
}
