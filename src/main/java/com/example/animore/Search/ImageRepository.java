package com.example.animore.Search;

import com.example.animore.Search.model.Image;
import com.example.animore.Search.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {

    List<Image> findByReview(Review review);
}
