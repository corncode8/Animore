package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.animore.model.Review;
import umc.animore.model.Store;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByStoreStoreId(Long storeId);

    List<Review> findByUserId(Long userId);

    Review findByReviewId(Long reviewId);

    //가게주소 - 후기 많은 순
    @Query("SELECT r.store FROM Review r WHERE r.store.storeLocation = :storeLocation GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReviewsByStoreLocationContaining(@Param("storeLocation") String storeLocation);

    //가게주소 - 후기

}

