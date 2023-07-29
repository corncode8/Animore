package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.Image;
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
        // 리뷰 작성 일자 설정
        Timestamp createdDate = new Timestamp(System.currentTimeMillis());
        review.setCreatedDate(createdDate);
        review.setModifiedDate(createdDate);
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
//            // 이미지를 업데이트합니다.
//            List<Image> imageList = updatedReview.getImages();
//            if (imageList != null && !imageList.isEmpty()) {
//                // 리뷰에 속한 모든 이미지를 삭제합니다.
//                imageService.deleteImagesByReview(existingReview);
//
//                // 새로운 이미지들을 추가합니다.
//                for (Image image : imageList) {
//                    Image newImage = new Image();
//                    newImage.setImgName(image.getImgName());
//                    newImage.setImgOriName(image.getImgOriName());
//                    newImage.setImgPath(image.getImgPath());
//                    newImage.setReview(existingReview);
//                    existingReview.getImages();
//                }
//            }

            // 변경사항을 저장합니다.
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

                if (updatedReview.getReviewContent() != null) {
                    existingReview.setReviewContent(updatedReview.getReviewContent());
                }
                if (updatedReview.getReviewLike() != null) {
                    existingReview.setReviewLike(updatedReview.getReviewLike());
                }
                if (updatedReview.getPetId() != null) {
                    existingReview.setPetId(updatedReview.getPetId());
                }

//            // 이미지 업데이트
//            List<Image> existingImages = existingReview.getImages();
//            List<Image> updatedImages = updatedReview.getImages();
//            if (existingImages != null && updatedImages != null) {
//                for (int i = 0; i < Math.min(existingImages.size(), updatedImages.size()); i++) {
//                    Image existingImage = existingImages.get(i);
//                    Image updatedImage = updatedImages.get(i);
//                    if (updatedImage.getImgName() != null) {
//                        existingImage.setImgName(updatedImage.getImgName());
//                    }
//                    if (updatedImage.getImgOriName() != null) {
//                        existingImage.setImgOriName(updatedImage.getImgOriName());
//                    }
//                    if (updatedImage.getImgPath() != null) {
//                        existingImage.setImgPath(updatedImage.getImgPath());
//                    }
//                }
//            }

            existingReview.setModifiedDate(new Timestamp(System.currentTimeMillis())); // 수정 날짜 업데이트
        }

        // 수정된 리뷰 저장!
        return reviewRepository.save(existingReview);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}