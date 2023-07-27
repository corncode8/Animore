package umc.animore.controller;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.controller.DTO.MypageHome;
import umc.animore.controller.DTO.MypageProfile;
import umc.animore.model.Image;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.service.ImageService;
import umc.animore.service.PetService;

import java.io.File;
import java.io.IOException;

@Controller
public class MypageController {

    @Autowired
    private PetService petService;

    @Autowired
    private ImageService imageService;



    /**
     * 마이페이지 첫화면 API
     */
    @GetMapping("/mypage")
    public MypageHome mypagehome(){
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pet pet = petService.findByUserId(principalDetails.getUser().getId());

        MypageHome mypageHome = MypageHome.builder()
                .nickname(pet.getUser().getNickname())
                .petName(pet.getPetName())
                .petAge(pet.getPetAge())
                .petType(pet.getPetType())
                .build();


        return mypageHome;
    }



    /**
     * 마이페이지 - 프로필수정  -> 프로필수정화면 API
     */
    // 이미지는 어떻게해야하나??
    @GetMapping("/mypage/profile")
    public MypageProfile mypageProfile(@RequestPart(value="multipartFile")MultipartFile multipartFile) {

        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        MypageProfile mypageProfile = MypageProfile.builder()
                .nickname(user.getNickname())
                .aboutMe(user.getAboutMe())
                .build();

        return mypageProfile;
    }

    /**
     *  프로필수정화면 - 사진 수정 API
     */
    @PostMapping("/mypage/profile/image")
    @ResponseBody
    public String uploadImage(@RequestPart MultipartFile multipartFile, @Value("${upload.path}") String url) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principalDetails.getUser().getId();


        Image image = new Image();

        String sourceFileName = multipartFile.getOriginalFilename();

        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

        FilenameUtils.removeExtension(sourceFileName);

        File destinationFile;
        String destinationFileName;

        String fileUrl = url;

        do{
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
            destinationFile = new File(fileUrl + destinationFileName);
        }while(destinationFile.exists());

        destinationFile.getParentFile().mkdirs();
        multipartFile.transferTo(destinationFile);

        image.setImgName(destinationFileName);
        image.setImgOriName(sourceFileName);
        image.setImgPath(fileUrl);

        imageService.save(image,userId);


        return "성공";
    }

    @GetMapping("/mypage/test")
    public String test(){
        return "testupload";
    }


    @ResponseBody
    @GetMapping("/mainImages")
    public Page<Image> getImagesByPage(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "3") int size,
                                       @RequestParam(defaultValue = "true") boolean isDiscounted) {
        return imageService.getImagesByPage(page, size, isDiscounted);
    }



}
