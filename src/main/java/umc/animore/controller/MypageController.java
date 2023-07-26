package umc.animore.controller;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseResponse;
import umc.animore.controller.DTO.MypageHome;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypagePetUpdate;
import umc.animore.controller.DTO.MypageProfile;
import umc.animore.model.Image;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.service.ImageService;
import umc.animore.service.PetService;
import umc.animore.service.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static umc.animore.config.exception.BaseResponseStatus.*;

@RestController
public class MypageController {

    @Autowired
    private PetService petService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;



    /**
     * 마이페이지 첫화면 API
     */
    @GetMapping("/mypage")
    public BaseResponse<MypageHome> mypagehome(){
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pet pet = petService.findTop1ByUser_idOrderByPetId(principalDetails.getUser().getId());

        MypageHome mypageHome = MypageHome.builder()
                .nickname(pet.getUser().getNickname())
                .petName(pet.getPetName())
                .petAge(pet.getPetAge())
                .petType(pet.getPetType())
                .build();



        return new BaseResponse<>(mypageHome);
    }



    /**
     * 마이페이지 - 프로필수정 click -> 와이어프레임.프로필수정 API
     */
    // 이미지는 어떻게해야하나?? 일단은 주소를 반환
    @GetMapping("/mypage/profile")
    public BaseResponse<MypageProfile> mypageProfile() {

        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        MypageProfile mypageProfile = MypageProfile.builder()
                .imgPath(user.getImage().getImgPath())
                .nickname(user.getNickname())
                .aboutMe(user.getAboutMe())
                .build();

        return new BaseResponse<>(mypageProfile);
    }

    /**
     *  와이어프레임.프로필수정 - 수정하기 API
     */
    @PostMapping("/mypage/profile")
    public BaseResponse<String> profileupdate(@RequestPart MultipartFile multipartFile,@RequestParam String nickname, @RequestParam String aboutMe, @Value("${upload_path}") String url) throws IOException {

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



        imageService.save(image,userId,nickname,aboutMe);


        return new BaseResponse<>(SUCCESS);
    }

    /**
     * 와이어프레임.회원정보수정 - 비밀번호 확인 API
     */
    @GetMapping("/mypage/member/user/password")
    public BaseResponse<String> userUpdatePasswordCheck(@RequestParam("password") String password){
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userPassword = principalDetails.getUser().getPassword();

        if(userPassword.equals(password)){
            return new BaseResponse<>("성공");
        }

        return new BaseResponse<>(REQUEST_ERROR);

    }

    /**
     * 와이어프레임.회원정보수정 API
     */
    @GetMapping("/mypage/member/user")
    public BaseResponse<MypageMemberUpdate> userupdate(){
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        MypageMemberUpdate mypageMemberUpdate = MypageMemberUpdate.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .gender(user.getGender())
                .build();

        return new BaseResponse<>(mypageMemberUpdate);
    }



    /**
     * 와이어프레임.회원정보수정 - 수정하기 API
     */
    @PostMapping("/mypage/member/user")
    public BaseResponse<String> userupdate(@RequestBody MypageMemberUpdate mypageMemberUpdate){
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principalDetails.getUser().getId();

        userService.saveMypageMemberUpdate(mypageMemberUpdate,userId);

        return new BaseResponse<>(SUCCESS);
    }


    /**
     * 와이어프레임.반려동물수정 API
     */
    @GetMapping("/mypage/member/pet")
    public BaseResponse<List<MypagePetUpdate>> petupdate(){

        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principalDetails.getUser().getId();



        return new BaseResponse<>(petService.findMypageMPetUpdateByUserId(userId));

    }

    /**
     * 와이어프레임.반려동물수정 - 반려동물수정하기 API
     */
    @PostMapping("/mypage/member/pet")
    public BaseResponse<String> petupdate(@RequestBody MypagePetUpdate mypagePetUpdate){
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principalDetails.getUser().getId();

        petService.saveMypagePetUpdate(mypagePetUpdate,userId);

        return new BaseResponse<>(SUCCESS);
    }








}
