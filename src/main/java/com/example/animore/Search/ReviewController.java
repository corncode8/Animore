package com.example.animore.Search;


import com.example.Config.BaseException;
import com.example.Config.BaseResponse;
import com.example.Config.BaseResponseStatus;
import com.example.animore.Search.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Config.BaseResponseStatus.EMPTY_REVIEW_CONTENT;
import static com.example.Config.BaseResponseStatus.EMPTY_REVIEW_LIKE;

@RestController
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService=reviewService;
    }

    //리뷰 작성
    @ResponseBody
    @PostMapping("/reviews/create")
    public BaseResponse<Review> createReview(@RequestBody Review review){
        try {
            Review createdReview = reviewService.createReview(review);
            return new BaseResponse<>(true, "리뷰 작성 성공", 1000, createdReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }


    @PutMapping("/reviews/update")
    public BaseResponse<Review> updateReview(@RequestBody Review review) {
        try {
            Review updatedReview = reviewService.updateReview(review);
            return new BaseResponse<>(true, "리뷰 수정 성공", 1000, updatedReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    @PatchMapping("/reviews/update/{reviewId}")
    public BaseResponse<Review> updatePartialReview(@PathVariable Long reviewId, @RequestBody Review review) {
        try {
            Review updatedReview = reviewService.updatePartialReview(reviewId,review);
            return new BaseResponse<>(true, "리뷰 수정 성공", 1000, updatedReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    //리뷰 삭제
    @ResponseBody
    @DeleteMapping("/reviews/{reviewId}")
    public BaseResponse<Review> deleteReview(@PathVariable Long reviewId) {
        try {
            Review deletedReview = reviewService.deleteReview(reviewId);
            return new BaseResponse<>(true, "리뷰 삭제 성공", 1000, deletedReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    //리뷰조회
    @ResponseBody
    @GetMapping("/reviews/{reviewId}")
    public BaseResponse<List<Review>> getReviewById(@PathVariable Long reviewId) {
        try{
            if (reviewId == null){
                return new BaseResponse<>(BaseResponseStatus.GET_SEARCH_EMPTY_QUERY);
            }
            List<Review> reviews = reviewService.getReviewById(reviewId);

            return new BaseResponse<>(reviews);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //특정 가게의 모든 리뷰 조회
    @ResponseBody
    @GetMapping("/reviews/store/{storeId}")
    public BaseResponse<List<Review>> getReviewsByStoreId(@PathVariable Long storeId){
        try{
            if (storeId == null){
                return new BaseResponse<>(BaseResponseStatus.GET_SEARCH_EMPTY_QUERY);
            }
            List<Review> reviews = reviewService.getReviewsByStoreId(storeId);

            return new BaseResponse<>(reviews);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
