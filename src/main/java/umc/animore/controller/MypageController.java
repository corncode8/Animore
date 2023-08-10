package umc.animore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.controller.DTO.*;
import umc.animore.model.Image;
import umc.animore.model.Pet;
import umc.animore.model.Reservation;
import umc.animore.model.User;
import umc.animore.repository.DTO.ReservationInfoMapping;
import umc.animore.service.ImageService;
import umc.animore.service.PetService;
import umc.animore.service.ReservationService;
import umc.animore.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Array;
import java.util.*;

import static umc.animore.config.exception.BaseResponseStatus.*;

@RestController
public class MypageController {

    @Autowired
    private PetService petService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;



    /**
     * 마이페이지 첫화면 API
     */
    @GetMapping("/mypage")
    public BaseResponse<MypageHome> mypagehome(){
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            Pet pet = petService.findTop1ByUser_idOrderByPetId(userId);

            if(pet == null){
                throw new BaseException(GET_PET_EMPTY_ERROR);
            }

            
            List<ReservationInfoMapping> reservationInfoMappings = reservationService.findByUserIdOrderByStartTimeDesc(userId);

            List<Map<Long, Object>> storeId_ImageUrl = imageService.findImageByReservationId(reservationInfoMappings);

            MypageHome mypageHome = MypageHome.builder()
                    .nickname(pet.getUser().getNickname())
                    .petName(pet.getPetName())
                    .petAge(pet.getPetAge())
                    .petType(pet.getPetType())
                    .storeId_ImageUrl(storeId_ImageUrl)
                    .build();


            return new BaseResponse<>(mypageHome);

        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());

        }
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
                    .imageUrls("http://www.animore.co.kr/reviews/images/"+user.getImage().getImgName())
                    .nickname(user.getNickname())
                    .aboutMe(user.getAboutMe())
                    .build();

            System.out.println(System.getProperty("user.dir"));
            return new BaseResponse<>(mypageProfile);


    }

    /**
     *  와이어프레임.프로필수정 - 수정하기 API
     */
    @PostMapping("/mypage/profile")
    public BaseResponse<MypageProfile> profileupdate(@RequestPart MultipartFile multipartFile,@RequestPart String nickname, @RequestPart String aboutMe, @Value("${upload.path}") String url) throws IOException {

        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = principalDetails.getUser();
            Long userId = user.getId();

            imageService.saveImage(multipartFile,userId,url);


            user = userService.saveNicknameAboutMe(userId,nickname,aboutMe);

            MypageProfile mypageProfile = MypageProfile.builder()
                    .nickname(user.getNickname())
                    .aboutMe(user.getAboutMe())
                    .imageUrls("http://www.animore.co.kr/reviews/images/"+user.getImage().getImgName())
                    .build();



            return new BaseResponse<>(mypageProfile);

        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 와이어프레임.회원정보수정 - 비밀번호 확인 API
     */
    @GetMapping("/mypage/member/user/password")
    public BaseResponse<String> userUpdatePasswordCheck(@RequestParam("password") String password){
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userPassword = principalDetails.getUser().getPassword();

            if (userPassword.equals(password)) {
                return new BaseResponse<>(SUCCESS);
            }else{
                throw new BaseException(GET_USER_PASSWORD_ERROR);
            }
        }
        catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * 와이어프레임.회원정보수정 API
     */
    @GetMapping("/mypage/member/user")
    public BaseResponse<MypageMemberUpdate> userupdate(){

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        MypageMemberUpdate mypageMemberUpdate = MypageMemberUpdate.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .build();

        return new BaseResponse<>(mypageMemberUpdate);



    }



    /**
     * 와이어프레임.회원정보수정 - 수정하기 API
     */
    @PostMapping("/mypage/member/user")
    public BaseResponse<MypageMemberUpdate> userupdate(@RequestBody MypageMemberUpdate mypageMemberUpdate){

        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();



            return new BaseResponse<>(userService.saveMypageMemberUpdate(mypageMemberUpdate, userId));

        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 와이어프레임.반려동물수정 API
     */
    @GetMapping("/mypage/member/pet")
    public BaseResponse<List<MypagePetUpdate>> petupdate(){
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();


            return new BaseResponse<>(petService.findMypageMPetUpdateByUserId(userId));
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 와이어프레임.반려동물수정 - 반려동물수정하기 API
     */
    @PostMapping("/mypage/member/pet")
    public BaseResponse<MypagePetUpdate> petupdate(@RequestBody MypagePetUpdate mypagePetUpdate){
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();



            return new BaseResponse<>(petService.saveMypagePetUpdate(mypagePetUpdate, userId));

        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PostMapping("/mypage/member/remove")
    public BaseResponse<String> memberCancel(){
        try{
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();

            userService.memberCancel(userId);

            return new BaseResponse<>(SUCCESS);

        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }



/* 프로필 이미지조회 방법 임시세이브

// 이미지는 어떻게해야하나?? 일단은 주소를 반환
@GetMapping("/mypage/profile")
public HttpEntity<LinkedMultiValueMap<String, Object>> mypageProfile(@Value("${upload.path}") String url) {
    try {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();
        Long userId = user.getId();
        Image img = imageService.findImageByUserId(userId);


        MypageProfile mypageProfile = MypageProfile.builder()
                .nickname(user.getNickname())
                .aboutMe(user.getAboutMe())
                .build();

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpStatus httpStatus = HttpStatus.CREATED;

        map.add("user관련 값", new BaseResponse<>(mypageProfile));

        map.add("사진 바이너리 값",
                new FileSystemResource(url + img.getImgName()));


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(map, headers);


        return requestEntity;

    }catch(BaseException e){
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpStatus httpStatus = HttpStatus.CREATED;

        map.add("에러관련 값", new BaseResponse<>(e.getStatus()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(map, headers);
        return requestEntity;
    }

}


 */



}
