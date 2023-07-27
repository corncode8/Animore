package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.Image;
import umc.animore.model.Review;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.model.review.ImageDTO;
import umc.animore.model.review.ReviewDTO;
import umc.animore.service.ImageService;
import umc.animore.service.ReviewService;
import umc.animore.service.StoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    private final ImageService imageService;

    @Autowired
    private final StoreService storeService;

    public ReviewController(ReviewService reviewService, ImageService imageService, StoreService storeService){
        this.reviewService=reviewService;
        this.imageService=imageService;
        this.storeService=storeService;
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

    @PutMapping("/reviews/update/{reviewId}")
    public BaseResponse<ReviewDTO> updateReview(@PathVariable Long reviewId,@RequestBody ReviewDTO reviewDTO) {
        try {
            Review review = reviewService.getReviewById(reviewId);

            review.setReviewContent(reviewDTO.getReviewContent());
            review.setReviewLike(reviewDTO.getReviewLike());
            review.setPetId(reviewDTO.getPetId());


            // 이미지 수정 로직 (기존 이미지들 삭제 후 새로운 이미지 추가)
            List<ImageDTO> imageDTOList = reviewDTO.getImages();
            if (imageDTOList != null && !imageDTOList.isEmpty()) {

                imageService.deleteImagesByReview(review); // 기존 이미지들 삭제

                for (ImageDTO imageDTO : imageDTOList) {
                    Image image = new Image();
                    image.setImgName(imageDTO.getImgName());
                    image.setImgOriName(imageDTO.getImgOriName());
                    image.setImgPath(imageDTO.getImgPath());
                    image.setReview(review);
                    review.getImages().add(image);
                }
            }

            Review updatedReview = reviewService.updateReview(review);

            // 업데이트된 리뷰 정보와 이미지 정보를 리턴
            ReviewDTO updatedReviewDTO = new ReviewDTO();
            updatedReviewDTO.setReviewId(updatedReview.getReviewId());
            updatedReviewDTO.setPetId(updatedReview.getPetId());
            updatedReviewDTO.setCreatedDate(updatedReview.getCreatedDate());
            updatedReviewDTO.setModifiedDate(updatedReview.getModifiedDate());
            updatedReviewDTO.setReviewContent(updatedReview.getReviewContent());
            updatedReviewDTO.setReviewLike(updatedReview.getReviewLike());
            updatedReviewDTO.setStoreId(updatedReview.getStore().getStoreId());
            updatedReviewDTO.setUserId(updatedReview.getUser().getId());

            List<Image> updatedImages = imageService.getImagesByReview(updatedReview);
            List<ImageDTO> updatedImageDTOList = new ArrayList<>();
            for (Image image : updatedImages) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImgName(image.getImgName());
                imageDTO.setImgOriName(image.getImgOriName());
                imageDTO.setImgPath(image.getImgPath());
                updatedImageDTOList.add(imageDTO);
            }
            updatedReviewDTO.setImages(updatedImageDTOList);

            return new BaseResponse<>(true, "리뷰 수정 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    /*
    * PATCH  http://localhost:8000/reviews/update/1
    * {
    "images":[
        {
            "imgOriName": "수정.jpg"
        }

        ]
    }
    *
    * */
    @PatchMapping("/reviews/update/{reviewId}")
    public BaseResponse<ReviewDTO> updatePartialReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        try {
                Review review = reviewService.getReviewById(reviewId);

                // 리뷰의 일부분만 업데이트
                if (reviewDTO.getReviewContent() != null) {
                    review.setReviewContent(reviewDTO.getReviewContent());
                }
                if (reviewDTO.getReviewLike() != null) {
                    review.setReviewLike(reviewDTO.getReviewLike());
                }
                if (reviewDTO.getPetId() != null) {
                    review.setPetId(reviewDTO.getPetId());
                }

                // 이미지를 부분적으로 수정
                List<ImageDTO> imageDTOList = reviewDTO.getImages();
                if (imageDTOList != null && !imageDTOList.isEmpty()) {
                    List<Image> images = imageService.getImagesByReview(review);
                    for (int i = 0; i < Math.min(images.size(), imageDTOList.size()); i++) {
                        ImageDTO imageDTO = imageDTOList.get(i);
                        Image image = images.get(i);
                        if (imageDTO.getImgName() != null) {
                            image.setImgName(imageDTO.getImgName());
                        }
                        if (imageDTO.getImgOriName() != null) {
                            image.setImgOriName(imageDTO.getImgOriName());
                        }
                        if (imageDTO.getImgPath() != null) {
                            image.setImgPath(imageDTO.getImgPath());
                        }
                    }
                }

                // 리뷰와 이미지 업데이트
                Review updatedReview = reviewService.updatePartialReview(reviewId,review);

                // 업데이트된 리뷰 정보와 이미지 정보를 리턴
                ReviewDTO updatedReviewDTO = new ReviewDTO();
                updatedReviewDTO.setReviewId(updatedReview.getReviewId());
                updatedReviewDTO.setPetId(updatedReview.getPetId());
                updatedReviewDTO.setCreatedDate(updatedReview.getCreatedDate());
                updatedReviewDTO.setModifiedDate(updatedReview.getModifiedDate());
                updatedReviewDTO.setReviewContent(updatedReview.getReviewContent());
                updatedReviewDTO.setReviewLike(updatedReview.getReviewLike());
                updatedReviewDTO.setStoreId(updatedReview.getStore().getStoreId());
                updatedReviewDTO.setUserId(updatedReview.getUser().getId());

                List<Image> updatedImages = imageService.getImagesByReview(updatedReview);
                List<ImageDTO> updatedImageDTOList = new ArrayList<>();
                for (Image image : updatedImages) {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setImageId(image.getImageId());
                    imageDTO.setImgName(image.getImgName());
                    imageDTO.setImgOriName(image.getImgOriName());
                    imageDTO.setImgPath(image.getImgPath());
                    updatedImageDTOList.add(imageDTO);
                }
                updatedReviewDTO.setImages(updatedImageDTOList);
            return new BaseResponse<>(true, "리뷰 수정 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    @PatchMapping("/reviews/update_add/{reviewId}")
    public BaseResponse<ReviewDTO> updatePartialReviewandAdd(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        try {
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰의 일부분만 업데이트
            if (reviewDTO.getReviewContent() != null) {
                review.setReviewContent(reviewDTO.getReviewContent());
            }
            if (reviewDTO.getReviewLike() != null) {
                review.setReviewLike(reviewDTO.getReviewLike());
            }
            if (reviewDTO.getPetId() != null) {
                review.setPetId(reviewDTO.getPetId());
            }

            // 이미지를 부분적으로 수정
            List<ImageDTO> imageDTOList = reviewDTO.getImages();
            if (imageDTOList != null && !imageDTOList.isEmpty()) {
                List<Image> images = imageService.getImagesByReview(review);
                for (int i = 0; i < Math.min(images.size(), imageDTOList.size()); i++) {
                    ImageDTO imageDTO = imageDTOList.get(i);
                    Image image = images.get(i);
                    if (imageDTO.getImgName() != null) {
                        image.setImgName(imageDTO.getImgName());
                    }
                    if (imageDTO.getImgOriName() != null) {
                        image.setImgOriName(imageDTO.getImgOriName());
                    }
                    if (imageDTO.getImgPath() != null) {
                        image.setImgPath(imageDTO.getImgPath());
                    }
                }
            }

            // 새로운 이미지 추가
            List<ImageDTO> newImageDTOList = reviewDTO.getNewImages();
            if (newImageDTOList != null && !newImageDTOList.isEmpty()) {
                for (ImageDTO newImageDTO : newImageDTOList) {
                    Image newImage = new Image();
                    newImage.setImgName(newImageDTO.getImgName());
                    newImage.setImgOriName(newImageDTO.getImgOriName());
                    newImage.setImgPath(newImageDTO.getImgPath());
                    // 새로운 이미지를 리뷰에 연결
                    review.addImage(newImage);
                }
            }

            // 리뷰와 이미지 업데이트
            Review updatedReview = reviewService.updatePartialReview(reviewId,review);

            // 업데이트된 리뷰 정보와 이미지 정보를 리턴
            ReviewDTO updatedReviewDTO = new ReviewDTO();
            updatedReviewDTO.setReviewId(updatedReview.getReviewId());
            updatedReviewDTO.setPetId(updatedReview.getPetId());
            updatedReviewDTO.setCreatedDate(updatedReview.getCreatedDate());
            updatedReviewDTO.setModifiedDate(updatedReview.getModifiedDate());
            updatedReviewDTO.setReviewContent(updatedReview.getReviewContent());
            updatedReviewDTO.setReviewLike(updatedReview.getReviewLike());
            updatedReviewDTO.setStoreId(updatedReview.getStore().getStoreId());
            updatedReviewDTO.setUserId(updatedReview.getUser().getId());

            List<Image> updatedImages = imageService.getImagesByReview(updatedReview);
            List<ImageDTO> updatedImageDTOList = new ArrayList<>();
            for (Image image : updatedImages) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImgName(image.getImgName());
                imageDTO.setImgOriName(image.getImgOriName());
                imageDTO.setImgPath(image.getImgPath());
                updatedImageDTOList.add(imageDTO);
            }
            updatedReviewDTO.setImages(updatedImageDTOList);
            return new BaseResponse<>(true, "리뷰 수정 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }


    //리뷰 삭제
    @ResponseBody
    @DeleteMapping("/reviews/{reviewId}")
    public BaseResponse<ReviewDTO> deleteReview(@PathVariable Long reviewId) {
        try {

            // 이미지 삭제 로직 (리뷰에 속한 이미지들 모두 삭제)
            imageService.deleteImagesByReviewId(reviewId);

            Review deletedReview = reviewService.deleteReview(reviewId);

            // 삭제된 리뷰의 ID를 ReviewDTO에 설정하여 반환
            ReviewDTO deletedReviewDTO = new ReviewDTO();
            deletedReviewDTO.setDeletedReviewId(reviewId);
            return new BaseResponse<>(true, "리뷰 삭제 성공", 1000, deletedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    //리뷰조회
    //http://localhost:8000/reviews/1
    @ResponseBody
    @GetMapping("/reviews/{reviewId}")
    public BaseResponse<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        try{
            if (reviewId == null) {
                return new BaseResponse<>(BaseResponseStatus.GET_SEARCH_EMPTY_QUERY);
            }

            Review review = reviewService.getReviewById(reviewId);
            List<Image> images = imageService.getImagesByReview(review);

            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setReviewId(review.getReviewId());
            reviewDTO.setPetId(review.getPetId());
            reviewDTO.setCreatedDate(review.getCreatedDate());
            reviewDTO.setModifiedDate(review.getModifiedDate());
            reviewDTO.setReviewContent(review.getReviewContent());
            reviewDTO.setReviewLike(review.getReviewLike());
            reviewDTO.setStoreId(review.getStore().getStoreId());
            reviewDTO.setUserId(review.getUser().getId());

            List<ImageDTO> imageDTOList = new ArrayList<>();
            for (Image image : images) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImgName(image.getImgName());
                imageDTO.setImgOriName(image.getImgOriName());
                imageDTO.setImgPath(image.getImgPath());
                imageDTOList.add(imageDTO);
            }
            reviewDTO.setImages(imageDTOList);

            return new BaseResponse<>(reviewDTO);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    //특정 가게의 모든 리뷰 조회
    //http://localhost:8000/reviews/store/1
    @ResponseBody
    @GetMapping("/reviews/store/{storeId}")
    public BaseResponse<List<ReviewDTO>> getReviewsByStoreId(@PathVariable Long storeId){
        try{
            if (storeId == null) {
                return new BaseResponse<>(BaseResponseStatus.GET_SEARCH_EMPTY_QUERY);
            }

            List<Review> reviews = reviewService.getReviewsByStoreId(storeId);

            List<ReviewDTO> reviewDTOList = new ArrayList<>();
            for (Review review : reviews) {
                List<Image> images = imageService.getImagesByReview(review);

                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setReviewId(review.getReviewId());
                reviewDTO.setPetId(review.getPetId());
                reviewDTO.setCreatedDate(review.getCreatedDate());
                reviewDTO.setModifiedDate(review.getModifiedDate());
                reviewDTO.setReviewContent(review.getReviewContent());
                reviewDTO.setReviewLike(review.getReviewLike());
                reviewDTO.setStoreId(review.getStore().getStoreId());
                reviewDTO.setUserId(review.getUser().getId());

                List<ImageDTO> imageDTOList = new ArrayList<>();
                for (Image image : images) {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setImageId(image.getImageId());
                    imageDTO.setImgName(image.getImgName());
                    imageDTO.setImgOriName(image.getImgOriName());
                    imageDTO.setImgPath(image.getImgPath());
                    imageDTOList.add(imageDTO);
                }
                reviewDTO.setImages(imageDTOList);

                reviewDTOList.add(reviewDTO);
            }

            return new BaseResponse<>(reviewDTOList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //특정 사용자의 모든 리뷰 조회
    //http://localhost:8000/reviews/user/1
    @ResponseBody
    @GetMapping("/reviews/user/{userId}")
    public BaseResponse<List<ReviewDTO>> getReviewsByUserId(@PathVariable Long userId){
        try{
            if (userId == null) {
                return new BaseResponse<>(BaseResponseStatus.GET_SEARCH_EMPTY_QUERY);
            }

            List<Review> reviews = reviewService.getReviewsById(userId);
            List<ReviewDTO> reviewDTOList = new ArrayList<>();
            for (Review review : reviews) {
                List<Image> images = imageService.getImagesByReview(review);

                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setReviewId(review.getReviewId());
                reviewDTO.setPetId(review.getPetId());
                reviewDTO.setCreatedDate(review.getCreatedDate());
                reviewDTO.setModifiedDate(review.getModifiedDate());
                reviewDTO.setReviewContent(review.getReviewContent());
                reviewDTO.setReviewLike(review.getReviewLike());
                reviewDTO.setStoreId(review.getStore().getStoreId());
                reviewDTO.setUserId(review.getUser().getId());

                List<ImageDTO> imageDTOList = new ArrayList<>();
                for (Image image : images) {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setImageId(image.getImageId());
                    imageDTO.setImgName(image.getImgName());
                    imageDTO.setImgOriName(image.getImgOriName());
                    imageDTO.setImgPath(image.getImgPath());
                    imageDTOList.add(imageDTO);
                }
                reviewDTO.setImages(imageDTOList);

                reviewDTOList.add(reviewDTO);
            }

            return new BaseResponse<>(reviewDTOList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}