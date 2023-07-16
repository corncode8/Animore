package com.example.animore.Search;

import com.example.animore.Search.model.Location;
import com.example.animore.Search.model.Town;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByLocationId(Integer locationId);

    Location findByLocationId(int locationId);



}