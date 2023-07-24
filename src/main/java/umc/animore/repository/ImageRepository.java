package umc.animore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Image;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImageId(Long Id);
}
