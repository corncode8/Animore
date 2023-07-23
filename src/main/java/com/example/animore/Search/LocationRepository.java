package com.example.animore.Search;

import com.example.animore.Search.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location, Long> {

    //Optional<Location> findByLocationId(Long locationId);
    Location findByLocationId(Long locationId);





}