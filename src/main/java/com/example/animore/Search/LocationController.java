package com.example.animore.Search;

import com.example.animore.Search.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
