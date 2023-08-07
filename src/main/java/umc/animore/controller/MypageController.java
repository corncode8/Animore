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
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.controller.DTO.MypageHome;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypagePetUpdate;
import umc.animore.controller.DTO.MypageProfile;
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
import java.io.IOException;
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
    public BaseResponse<MypageProfile> profileupdate(@RequestPart MultipartFile multipartFile,@RequestPart String nickname, @RequestPart String aboutMe, @Value("${upload.path}") String url) throws IOException {

        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = principalDetails.getUser();
            Long userId = user.getId();


            Image image = new Image();

            String sourceFileName = multipartFile.getOriginalFilename();

            String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

            FilenameUtils.removeExtension(sourceFileName);

            File destinationFile;
            String destinationFileName;

            String fileUrl = url;

            do {
                destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
                destinationFile = new File(fileUrl + destinationFileName);
            } while (destinationFile.exists());

            destinationFile.getParentFile().mkdirs();
            multipartFile.transferTo(destinationFile);

            image.setImgName(destinationFileName);
            image.setImgOriName(sourceFileName);
            image.setImgPath(fileUrl);


            imageService.save(image, userId, nickname, aboutMe);

            user = userService.getUserId(userId);

            MypageProfile mypageProfile = MypageProfile.builder()
                    .imgPath(user.getImage().getImgPath())
                    .nickname(user.getNickname())
                    .aboutMe(user.getAboutMe())
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








}
