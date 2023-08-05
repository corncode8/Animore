package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByStoreStoreId(Long storeId);

    List<Review> findByUserId(Long userId);

    Review findByReviewId(Long reviewId);
}

