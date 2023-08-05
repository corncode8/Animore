package umc.animore.repository;

import umc.animore.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {


    Location findByLocationId(Long locationId);

}