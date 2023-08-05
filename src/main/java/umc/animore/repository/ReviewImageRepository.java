package umc.animore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Image;
import umc.animore.model.Review;
import umc.animore.model.ReviewImage;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReview(Review review);
}

