package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.Review;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.repository.ReviewRepository;

import java.sql.Timestamp;
import java.util.List;

import static umc.animore.config.exception.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ImageService imageService;

    @Autowired
    ReviewService(ReviewRepository reviewRepository, ImageService imageService){
        this.reviewRepository = reviewRepository;
        this.imageService=imageService;
    }

    //리뷰 작성
    public Review createReview(Review review, Store store, User user) throws BaseException {

        review.setStore(store);
        review.setUser(user);
        return reviewRepository.save(review);
    }

    //리뷰 조회 (특정 가게의 모든 리뷰)
    public List<Review> getReviewsByStoreId(Long storeId) throws BaseException {
        try {
            List<Review> reviews = reviewRepository.findByStoreStoreId(storeId);
            return reviews;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //리뷰 조회 (특정 사용자의 모든 리뷰)
    public List<Review> getReviewsById(Long userId) throws BaseException {
        try {
            List<Review> reviews = reviewRepository.findByUserId(userId);
            return reviews;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //리뷰 조회 (리뷰 id)
    public Review getReviewById(Long reviewId) throws BaseException {
        try {
            return reviewRepository.findByReviewId(reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //리뷰 삭제
    public Review deleteReview(Long reviewId) throws BaseException  {
        Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_MATCHING_STORE));

        reviewRepository.deleteById(reviewId);
        return deletedReview;
    }

    // 리뷰 전체 수정
    public Review updateReview(Review updatedReview) throws BaseException {
        try {
            Review existingReview = reviewRepository.findById(updatedReview.getReviewId()).orElse(null);
            if (existingReview !=null){
                existingReview.setReviewContent(updatedReview.getReviewContent());
                existingReview.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                existingReview.setPetId(updatedReview.getPetId());
                existingReview.setReviewLike(updatedReview.getReviewLike());
            }
            // 수정된 리뷰 저장
            return reviewRepository.save(existingReview);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 리뷰 부분 수정
    public Review updatePartialReview(Long reviewId, Review updatedReview) throws BaseException {
        try {
            Review existingReview = reviewRepository.findById(reviewId).orElse(null);
            if (existingReview != null) {
                // 리뷰의 일부 필드만 업데이트
                if (updatedReview.getReviewContent() != null) {
                    existingReview.setReviewContent(updatedReview.getReviewContent());
                    existingReview.setPetId(updatedReview.getPetId());
                    existingReview.setReviewLike(updatedReview.getReviewLike());
                    existingReview.setModifiedDate(new Timestamp(System.currentTimeMillis())); // 수정 날짜 업데이트
                }

            }
            // 수정된 리뷰 저장
            return reviewRepository.save(existingReview);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}