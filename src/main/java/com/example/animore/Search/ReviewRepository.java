package com.example.animore.Search;

import com.example.animore.Search.model.Review;
import com.example.animore.Search.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByStoreStoreIdx(Integer storeIdx);

    List<Review> findByReviewId(Integer reviewId);

}
