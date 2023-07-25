package com.example.animore.Search;


import com.example.Config.BaseException;
import com.example.Config.BaseResponse;
import com.example.Config.BaseResponseStatus;
import com.example.animore.Search.model.Image;
import com.example.animore.Search.model.Review;
import com.example.animore.Search.model.Store;
import com.example.animore.Search.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.Config.BaseResponseStatus.EMPTY_REVIEW_CONTENT;
import static com.example.Config.BaseResponseStatus.EMPTY_REVIEW_LIKE;

@RestController
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    private final ImageService imageService;

    public ReviewController(ReviewService reviewService, ImageService imageService){
        this.reviewService=reviewService;
        this.imageService=imageService;
    }

    //리뷰 작성 - storeId, userId 직접입력
    //http://localhost:8000/reviews/create?storeId=4&userId=1
    @ResponseBody
    @PostMapping("/reviews/create")
    public BaseResponse<Review> createReview(@RequestBody Review review, @RequestParam Long storeId, @RequestParam Long userId, @RequestParam(value = "images", required = false) List<MultipartFile> images){
        // 이미지 개수 체크
        if (images != null && images.size() > 3) {
            return new BaseResponse<>(false, "이미지는 최대 3개까지만 등록할 수 있습니다.", 6000, null);
        }

        try {

            // 이미지 저장 로직 (images 컬렉션에 이미지 추가)
            if (images != null) {
                for (MultipartFile imageFile : images) {
                    // 이미지를 저장하고 Review와의 관계를 설정한 후 images 컬렉션에 추가
                    Image image = imageService.saveImage(imageFile, review);
                    review.getImages().add(image);
                }
            }

            Store store = new Store();
            store.setStoreId(storeId);
            User user = new User();
            user.setUserId(userId);
            Review createdReview = reviewService.createReview(review,store,user);
            return new BaseResponse<>(true, "리뷰 작성 성공", 1000, createdReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    /*

    //리뷰 작성2 - storeId 직접입력
    //http://localhost:8000/reviews/create2?storeId=4
    @ResponseBody
    @PostMapping("/reviews/create2")
    public BaseResponse<Review> createReview2(@RequestBody Review review, @RequestParam Long storeId){
        try {
            Store store = new Store();
            store.setStoreId(storeId);
            // 리뷰 작성자 ID는 클라이언트에서 로그인 정보 등을 통해 얻어올 수 있을 것으로 가정하고, 이를 리뷰 객체에 설정
            Long userId = getCurrentUserId(); // 현재 로그인한 사용자의 ID를 얻어오는 메서드 (예시)
            User user = userService.getUserById(userId);
            review.setUser(user);
            Review createdReview = reviewService.createReview(review,store,user);
            return new BaseResponse<>(true, "리뷰 작성 성공", 1000, createdReview);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }
*/



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
            List<Review> review = reviewService.getReviewById(reviewId);
            List<Image> images = imageService.getImagesByReview(review);

            return new BaseResponse<>(review);
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
