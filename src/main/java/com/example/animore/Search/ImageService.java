package com.example.animore.Search;

import com.example.Config.BaseException;
import com.example.Config.BaseResponseStatus;
import com.example.animore.Search.model.Image;
import com.example.animore.Search.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.Config.BaseResponseStatus.IMAGE_UPLOAD_ERROR;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    // 이미지를 저장하는 경로 설정 (이 부분은 실제 프로젝트에 맞게 변경해야 합니다)
    private static final String UPLOAD_DIR = "/path/to/upload/directory/";

    public Image saveImage(MultipartFile imageFile, Review review) throws BaseException {
        // 이미지 업로드 로직
        String imageName = saveImageFile(imageFile);

        // Image 엔티티 생성 및 설정
        Image image = new Image();
        image.setImgName(imageName);
        image.setImgOriName(imageFile.getOriginalFilename());
        image.setImgPath(UPLOAD_DIR);
        image.setReview(review);

        // Image 엔티티 저장 (이미지 정보를 DB에 저장)
        imageRepository.save(image);

        return image;
    }

    // 이미지를 실제로 저장하는 로직
    private String saveImageFile(MultipartFile imageFile) throws BaseException {
        // 이미지 저장 로직 (예시로 파일을 랜덤한 이름으로 저장하도록 구현)
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String imageName = UUID.randomUUID().toString() + extension;

        File file = new File(UPLOAD_DIR + imageName);
        try {
            imageFile.transferTo(file);
        } catch (Exception exception) {
            throw new BaseException(IMAGE_UPLOAD_ERROR);
        }

        return imageName;
    }

    //조회
    // 특정 리뷰에 연결된 이미지 목록을 가져오는 메서드
    public List<Image> getImagesByReview(List<Review> reviews) {
        List<Image> images = new ArrayList<>();
        for (Review review : reviews) {
            images.addAll(imageRepository.findByReview(review));
        }
        return images;
    }
}
