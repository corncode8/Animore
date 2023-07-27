package umc.animore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Image;
import umc.animore.model.Review;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImageId(Long Id);
    Page<Image> findByStoreIsDiscounted(boolean isDiscounted, Pageable pageable);

    List<Image> findByReview(Review review);


}
