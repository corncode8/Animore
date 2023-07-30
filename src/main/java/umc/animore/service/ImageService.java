package umc.animore.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.*;
import umc.animore.model.review.ImageDTO;
import umc.animore.repository.*;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static umc.animore.config.exception.BaseResponseStatus.NOT_FOUND_REVIEW;
import static umc.animore.config.exception.BaseResponseStatus.RESPONSE_ERROR;

//import static umc.animore.config.exception.BaseResponseStatus.IMAGE_UPLOAD_ERROR;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    StoreRepository storeRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public Page<Image> getImagesByPage(int pageNumber, int pageSize, boolean isDiscounted) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return imageRepository.findByStoreIsDiscounted(isDiscounted, pageable);
    }

    @Transactional
    public void save(Image image,Long userId,String nickname, String aboutMe) throws BaseException{
        try {
            User user = userRepository.findById(userId);
            user.setNickname(nickname);
            user.setAboutMe(aboutMe);

            Image img = user.getImage();

            if (img == null) {
                img = new Image();
                img.setUser(user);
            }

            img.setImgName(image.getImgName());
            img.setImgOriName(image.getImgOriName());
            img.setImgPath(image.getImgPath());

            imageRepository.save(img);

        }catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }


    }



    public Image saveImage(MultipartFile imageFile, Long reviewId, Long userId, Long storeId) {

       String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image";
       UUID uuid = UUID.randomUUID();

       String originalFileName = uuid+"_"+imageFile.getOriginalFilename();

       File saveFile = new File(projectPath, originalFileName);

       Image image = new Image();

        // 사용자, 리뷰, 예약 정보 가져오기 (userRepository, reviewRepository, reservationRepository는 이미 정의되어 있다고 가정)
        User user = userRepository.findById(userId);
        Store store = storeRepository.findByStoreId(storeId);




        // 이미지 파일 저장 로직
        try {
            imageFile.transferTo(saveFile);

            image.setImgName(originalFileName);
            image.setImgOriName(imageFile.getOriginalFilename());
            image.setImgPath(saveFile.getAbsolutePath());
            image.setUser(userRepository.findById(userId));
            image.setReview(reviewRepository.findByReviewId(reviewId));
            image.setStore(storeRepository.findByStoreId(storeId));
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
        }
        // Image 엔티티를 반환하여 리뷰와 연결할 수 있도록 함
        return image;
    }


    //조회
    // 특정 리뷰에 연결된 이미지 목록을 가져오는 메서드
    public List<Image> getImagesByReview(Review review) {
        return imageRepository.findByReview(review);

    }

    public void deleteImagesByReviewId(Long reviewId) throws BaseException {

        // 리뷰 ID로 해당 리뷰를 조회합니다.
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_REVIEW));
        // 리뷰에 속한 모든 이미지를 조회합니다.
        List<Image> images = imageRepository.findByReview(review);

        // 조회한 이미지들을 하나씩 삭제합니다.
        for (Image image : images) {
            // 이미지 삭제
            imageRepository.delete(image);
        }
    }

    public void deleteImagesByReview(Review review) throws BaseException {

        // 리뷰에 속한 모든 이미지를 조회합니다.
        List<Image> images = imageRepository.findByReview(review);

        // 조회한 이미지들을 하나씩 삭제합니다.
        for (Image image : images) {
            // 이미지 삭제
            imageRepository.delete(image);
        }
    }

//    public void addImagesToReview(Review review, List<ImageDTO> imageDTOList, int maxImagesToAdd) throws BaseException {
//        // 기존 리뷰에 속한 이미지들을 조회합니다.
//        List<Image> existingImages = imageRepository.findByReview(review);
//
//        // 이미지를 최대 maxImagesToAdd개까지 추가합니다.
//        int count = existingImages.size();
//        for (ImageDTO imageDTO : imageDTOList) {
//            if (count >= maxImagesToAdd) {
//                break;
//            }
//            Image image = new Image();
//            image.setImgName(imageDTO.getImgName());
//            image.setImgOriName(imageDTO.getImgOriName());
//            image.setImgPath(imageDTO.getImgPath());
//            image.setReview(review);
//            review.getImages().add(image);
//            count++;
//        }
//
//        // 이미지 개수가 최대 개수보다 적을 경우 나머지 이미지들을 삭제합니다.
//        if (existingImages.size() > maxImagesToAdd) {
//            for (int i = maxImagesToAdd; i < existingImages.size(); i++) {
//                Image image = existingImages.get(i);
//                review.getImages().remove(image);
//                imageRepository.delete(image);
//            }
//        }
//    }



}