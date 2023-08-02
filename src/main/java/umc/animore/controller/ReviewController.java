package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.*;
import umc.animore.model.review.ImageDTO;
import umc.animore.model.review.ReservationResultDTO;
import umc.animore.model.review.ReviewDTO;
import umc.animore.model.review.StoreDTO;
import umc.animore.repository.*;
import umc.animore.service.ImageService;
import umc.animore.service.ReviewService;
import umc.animore.service.StoreService;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static umc.animore.config.exception.BaseResponseStatus.*;

@RestController
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    private final ImageService imageService;

    @Autowired
    private final StoreService storeService;

    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private final StoreRepository storeRepository;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ReservationRepository reservationRepository;

    @Autowired
    private final ImageRepository imageRepository;

    public ReviewController(ReviewService reviewService, ImageService imageService, StoreService storeService,
                            ReviewRepository reviewRepository, StoreRepository storeRepository, UserRepository userRepository,
                            ReservationRepository reservationRepository, ImageRepository imageRepository) {
        this.reviewService = reviewService;
        this.imageService = imageService;
        this.storeService = storeService;
        this.reviewRepository = reviewRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.imageRepository = imageRepository;
    }

    //리뷰 작성 - storeId, userId 직접입력
    //http://localhost:8000/reviews/create?storeId=4&userId=1
    @ResponseBody
    @PostMapping("/reviews/create")
    public BaseResponse<ReviewDTO> createReview(@RequestParam Long storeId, @RequestParam Long petId, @RequestParam String reviewContent,
                                                @RequestParam Double reviewLike, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        // 이미지 개수 체크
        if (images != null && images.size() > 3) {
            return new BaseResponse<>(false, "이미지는 최대 3개까지만 등록할 수 있습니다.", 6000, null);
        }

        if (petId == null || petId.equals("")) {
            return new BaseResponse<>(EMPTY_PET_ID);
        }

        if (reviewContent == null || reviewContent.equals("")) {
            return new BaseResponse<>(EMPTY_REVIEW_CONTENT);
        }

        if (reviewLike == null || reviewLike.equals("")) {
            return new BaseResponse<>(EMPTY_REVIEW_LIKE);
        }


        try {
            // 리뷰 객체 생성
            Review review = new Review();
            Store store = new Store();
            store.setStoreId(storeId);

            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            User user = new User();
            user.setId(userId);

            // 해당 가게와 사용자에 대한 예약 정보 가져오기
            Reservation reservation = reservationRepository.findByUserAndStore(user,store);

            if (reservation==null){
                return new BaseResponse<>(BaseResponseStatus.NOT_FOUND_RESERVATION);
            }

            review.setPetId(petId);
            review.setReviewContent(reviewContent);
            review.setReviewLike(reviewLike);
            review.setUser(user);
            review.setStore(store);

            // 리뷰 생성
            Review createdReview = reviewService.createReview(review, store, user);

            // 이미지 저장 로직 (images 컬렉션에 이미지 추가)
            if (images != null) {
                for (MultipartFile imageFile : images) {
                    // 이미지를 저장하고 Review와의 관계를 설정한 후 images 컬렉션에 추가
                    Image image = new Image();
                    image.setReview(createdReview); // 리뷰와 연결
                    image.setStore(createdReview.getStore()); // 가게와 연결
                    image.setUser(createdReview.getUser()); // 사용자와 연결

                    List<Image> savedImages = imageService.saveImages(images, createdReview.getReviewId(), storeId, userId);
                    createdReview.getImages().addAll(savedImages);
                }
            }


            // 업데이트된 리뷰 정보와 이미지 정보를 리턴
            ReviewDTO createdReviewDTO = new ReviewDTO();
            createdReviewDTO.setReviewId(createdReview.getReviewId());
            createdReviewDTO.setPetId(createdReview.getPetId());
            createdReviewDTO.setCreatedDate(createdReview.getCreatedDate());
            createdReviewDTO.setModifiedDate(createdReview.getModifiedDate());
            createdReviewDTO.setReviewContent(createdReview.getReviewContent());
            createdReviewDTO.setReviewLike(createdReview.getReviewLike());
            createdReviewDTO.setStoreId(createdReview.getStore().getStoreId());
            createdReviewDTO.setUserId(createdReview.getUser().getId());

            List<Image> updatedImages = imageService.getImagesByReview(createdReview);
            List<ImageDTO> updatedImageDTOList = new ArrayList<>();
            for (Image image : updatedImages) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImgName(image.getImgName());
                imageDTO.setImgOriName(image.getImgOriName());
                imageDTO.setImgPath(image.getImgPath());
                updatedImageDTOList.add(imageDTO);
            }
            createdReviewDTO.setImages(updatedImageDTOList);

            return new BaseResponse<>(true, "리뷰 작성 성공", 1000, createdReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    //이미지 추가
    @ResponseBody
    @PostMapping("/reviews/{reviewId}/images")
    public BaseResponse<ReviewDTO> uploadImages(@PathVariable Long reviewId, @RequestPart(value = "images") MultipartFile imageFile) {

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principalDetails.getUser().getId();

        // 이미지 파일 저장 경로
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";
        UUID uuid = UUID.randomUUID();
        String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
        File saveFile = new File(projectPath + originalFileName);

        Review review = reviewRepository.findByReviewId(reviewId);
        Long reviewUserId = review.getUser().getId();


        if (userId != reviewUserId){
            return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
        }

        Long storeId = review.getStore().getStoreId();

        User user = new User();
        Store store = new Store();
        user.setId(userId);
        store.setStoreId(storeId);

        Reservation reservation = reservationRepository.findByUserAndStore(user, store);

        if(reservation == null){
            return new BaseResponse<>(BaseResponseStatus.NOT_FOUND_RESERVATION);
        }


        // 이미지 파일 저장 로직
        try {
            imageFile.transferTo(saveFile);

            // 이미지 메타데이터 DB에 저장
            Image image = new Image();
            image.setImgName(originalFileName);
            image.setImgOriName(imageFile.getOriginalFilename());
            image.setImgPath(saveFile.getAbsolutePath());
            image.setUser(userRepository.findById(userId));
            image.setReview(reviewRepository.findByReviewId(reviewId));
            image.setStore(storeRepository.findByStoreId(storeId));
            imageRepository.save(image);

            List<Image> images = imageRepository.findByReview(review);

            // 리뷰 정보와 이미지 정보를 리턴
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
            for (Image img : images) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(img.getImageId());
                imageDTO.setImgName(img.getImgName());
                imageDTO.setImgOriName(img.getImgOriName());
                imageDTO.setImgPath(img.getImgPath());
                imageDTOList.add(imageDTO);
            }

            reviewDTO.setImages(imageDTOList);

            return new BaseResponse<>(true, "리뷰 이미지 추가 성공", 1000, reviewDTO);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
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
    //전체수정 - 리뷰내용만 수정됨, 이미지 전체 삭제
    @PutMapping("/reviews/update/{reviewId}")
    public BaseResponse<ReviewDTO> updateReview(@PathVariable Long reviewId,@RequestBody ReviewDTO reviewDTO, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            // ReviewDTO가 null인지 체크
            if (reviewDTO == null || reviewDTO.equals("")) {
                return new BaseResponse<>(EMPTY_REVIEW_DTO);
            }

            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            // 리뷰 정보 가져오기
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰 작성자와 현재 사용자가 같은지 확인
            Long reviewUserId = review.getUser().getId();
            if(userId != reviewUserId){
                return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
            }

            // 리뷰 정보 업데이트
            review.setReviewContent(reviewDTO.getReviewContent());
            review.setReviewLike(reviewDTO.getReviewLike());
            review.setPetId(reviewDTO.getPetId());

            // 기존 이미지들 삭제
            imageService.deleteImagesByReviewId(reviewId);

            // 새로운 이미지 추가 (images가 null이 아니고 비어있지 않을 때)
            if (images != null && !images.isEmpty()) {
                List<Image> newImages = new ArrayList<>();
                for (MultipartFile imageFile : images) {
                    Image image = saveImage(imageFile, reviewId);
                    newImages.add(image);
                }
                review.setImages(newImages); // 새로운 이미지로 업데이트
            }
            // 리뷰 업데이트
            Review updatedReview = reviewService.updateReview(review);

            // 업데이트된 리뷰 정보를 DTO로 변환하여 리턴
            ReviewDTO updatedReviewDTO = convertReviewToDTO(updatedReview);

            return new BaseResponse<>(true, "리뷰 전체 수정 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    private Image saveImage(MultipartFile imageFile, Long reviewId) {
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";
        UUID uuid = UUID.randomUUID();
        String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
        File saveFile = new File(projectPath + originalFileName);

        Review review = reviewRepository.findByReviewId(reviewId);
        Long userId = review.getUser().getId();
        Long storeId = review.getStore().getStoreId();

        User user = userRepository.findById(userId);
        Store store = storeRepository.findByStoreId(storeId);

        // 예약 정보 조회
        Reservation reservation = reservationRepository.findByUserAndStore(user, store);

        Image image = new Image();
        image.setImgName(originalFileName);
        image.setImgOriName(imageFile.getOriginalFilename());
        image.setImgPath(saveFile.getAbsolutePath());
        image.setUser(user);
        image.setReview(review);
        image.setStore(store);

        try {
            imageFile.transferTo(saveFile);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
        }
    }

    private ReviewDTO convertReviewToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setPetId(review.getPetId());
        reviewDTO.setCreatedDate(review.getCreatedDate());
        reviewDTO.setModifiedDate(review.getModifiedDate());
        reviewDTO.setReviewContent(review.getReviewContent());
        reviewDTO.setReviewLike(review.getReviewLike());
        reviewDTO.setStoreId(review.getStore().getStoreId());
        reviewDTO.setUserId(review.getUser().getId());

        List<Image> updatedImages = imageService.getImagesByReview(review);
        List<ImageDTO> updatedImageDTOList = new ArrayList<>();
        for (Image image : updatedImages) {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setImageId(image.getImageId());
            imageDTO.setImgName(image.getImgName());
            imageDTO.setImgOriName(image.getImgOriName());
            imageDTO.setImgPath(image.getImgPath());
            updatedImageDTOList.add(imageDTO);
        }
        reviewDTO.setImages(updatedImageDTOList);

        return reviewDTO;
    }


    //PATCH  http://localhost:8000/reviews/update/1
    //부분 수정
    @PatchMapping("/reviews/update/{reviewId}")
    public BaseResponse<ReviewDTO> updatePartialReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            // 리뷰 정보 가져오기
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰 작성자와 현재 사용자가 같은지 확인
            Long reviewUserId = review.getUser().getId();

            if(userId !=  reviewUserId){
                return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
            }

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
            Review updatedReview = reviewService.updatePartialReview(reviewId, review);

            // 업데이트된 리뷰 정보를 DTO로 변환하여 리턴
            ReviewDTO updatedReviewDTO = convertReviewToDTO(updatedReview);

            return new BaseResponse<>(true, "리뷰 부분 수정 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }

    //이미지만 삭제하고 싶을때
    @DeleteMapping("/reviews/update/{reviewId}/image")
    public BaseResponse<ReviewDTO> updatePartialReviewandDelete(@PathVariable Long reviewId) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            // 리뷰 정보 가져오기
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰 작성자와 현재 사용자가 같은지 확인
            Long reviewUserId = review.getUser().getId();

            if(userId != reviewUserId){
                return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
            }

            imageService.deleteImagesByReviewId(reviewId); // 기존 이미지들 삭제

            // 리뷰 업데이트
            Review updatedReview = reviewService.updatePartialReview(reviewId, review);

            // 업데이트된 리뷰 정보를 DTO로 변환하여 리턴
            ReviewDTO updatedReviewDTO = convertReviewToDTO(updatedReview);

            return new BaseResponse<>(true, "리뷰 이미지 삭제 성공", 1000, updatedReviewDTO);
        } catch (BaseException exception) {
            return new BaseResponse<>(false, exception.getStatus().getMessage(), exception.getStatus().getCode(), null);
        }
    }


    //리뷰 삭제
    @ResponseBody
    @DeleteMapping("/reviews/{reviewId}")
    public BaseResponse<ReviewDTO> deleteReview(@PathVariable Long reviewId) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            // 리뷰 정보 가져오기
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰 작성자와 현재 사용자가 같은지 확인
            Long reviewUserId = review.getUser().getId();
            if(userId != reviewUserId){
                return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
            }

            // 이미지 삭제 로직 (리뷰에 속한 이미지들 모두 삭제)
            imageService.deleteImagesByReviewId(reviewId);

            // 리뷰 삭제
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

            // 현재 인증된 사용자 정보 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            // 리뷰 정보 가져오기
            Review review = reviewService.getReviewById(reviewId);

            // 리뷰 작성자와 현재 사용자가 같은지 확인
            Long reviewUserId = review.getUser().getId();
            if(userId != reviewUserId){
                return new BaseResponse<>(BaseResponseStatus.NOT_REVIEWER_USER);
            }

            // 리뷰 정보를 DTO로 변환하여 리턴
            ReviewDTO reviewDTO = convertReviewToDTO(review);

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
    //http://localhost:8000/reviews/user
    @ResponseBody
    @GetMapping("/reviews/user")
    public BaseResponse<List<ReviewDTO>> getReviewsByUserId(){
        try{
            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            List<Review> reviews = reviewService.getReviewsById(userId);


            if (reviews == null) {
                return new BaseResponse<>(BaseResponseStatus.NOT_FOUND_REVIEW);
            }


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

    //리뷰할 가게 및 예약 정보
    @ResponseBody
    @GetMapping("/reviews/researvationinfo/{storeId}")
    public BaseResponse<ReservationResultDTO> getReservationInfoByUser(@PathVariable Long storeId) {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            User user = userRepository.findById(userId);
            Store store = storeRepository.findByStoreId(storeId);
            //해당 가게의 예약횟수 조회
            store.setStoreRecent(reservationRepository.findStoreWithHighestReservationCount());

            // 해당 가게와 사용자에 대한 예약 정보 가져오기
            Reservation reservation = reservationRepository.findByUserAndStore(user, store);

            if (reservation == null) {
                return new BaseResponse<>(BaseResponseStatus.NOT_FOUND_RESERVATION);
            }

            ReservationResultDTO reservationResultDTO = new ReservationResultDTO();

            reservationResultDTO.setReservationId(reservation.getReservationId());
            reservationResultDTO.setPet_name(reservation.getPet_name());
            reservationResultDTO.setPet_gender(reservation.getPet_gender());
            reservationResultDTO.setDogSize(reservation.getDogSize());
            reservationResultDTO.setCutStyle(reservation.getCutStyle());
            reservationResultDTO.setBathStyle(reservation.getBathStyle());
            reservationResultDTO.setStartTime(reservation.getStartTime());
            reservationResultDTO.setUsername(reservation.getUsername());
            reservationResultDTO.setPet_type(reservation.getPet_type());

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setStoreId(store.getStoreId());
            storeDTO.setStoreName(store.getStoreName());
            storeDTO.setStoreExplain(store.getStoreExplain());
            storeDTO.setStoreLocation(store.getStoreLocation());
            storeDTO.setStoreImageUrl(store.getStoreImageUrl());
            storeDTO.setStoreNumber(store.getStoreNumber());
            storeDTO.setStoreRecent(store.getStoreRecent());
            storeDTO.setStoreLike(store.getStoreLike());
            storeDTO.setCreateAt(store.getCreateAt());
            storeDTO.setModifyAt(store.getModifyAt());
            storeDTO.setLatitude(store.getLatitude());
            storeDTO.setLongitude(store.getLongitude());
            storeDTO.setDiscounted(store.isDiscounted());
            storeDTO.setOpen(store.getOpen());
            storeDTO.setClose(store.getClose());
            storeDTO.setAmount(store.getAmount());
            storeDTO.setDayoff1(store.getDayoff1());
            storeDTO.setDayoff2(store.getDayoff2());

            reservationResultDTO.setStoreDTO(storeDTO);

            return new BaseResponse<>(reservationResultDTO);
        } catch (Exception exception) {
            return new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR); // Or handle the exception accordingly
        }
    }

}