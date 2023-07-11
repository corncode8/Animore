package com.example.animore.Search;

import com.example.animore.Search.model.Review;
import com.example.animore.Search.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    //List<Object[]> countReviewsByStore();
}
