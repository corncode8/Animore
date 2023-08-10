package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.exception.BaseException;
import umc.animore.controller.DTO.MypageMember;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypageProfile;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static umc.animore.config.exception.BaseResponseStatus.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetService petService;

    @Transactional
    public User getUserId(Long userId) throws BaseException{

        try{
            return userRepository.findById(userId);
        }catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

    // 예약상세 저장내용 불러오기
    // 유저정보 + 펫정보 불러오기
    @Transactional
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저를 찾지 못했습니다");
        }
        Pet pet = petService.getPetInfo(user);
        if (pet == null) {
            throw new IllegalArgumentException("해당 유저의 펫 정보를 찾지 못했습니다.");
        }
        Map<String, Object> userinfoMap = new HashMap<>();
        userinfoMap.put("petName", pet.getPetName());
        userinfoMap.put("petType", pet.getPetType());
        userinfoMap.put("petGender", pet.getPetGender());
        userinfoMap.put("nickname", user.getNickname());
        userinfoMap.put("phone", user.getPhone());
        userinfoMap.put("address", user.getAddress());
        return userinfoMap;
    }


    /**
     * mypageMemberUpdate를 이용하여 user 업데이트
     **/
    @Transactional
    public MypageMember saveMypageMemberUpdate(MypageMemberUpdate mypageMemberUpdate, Long userId) throws BaseException {

        try {



            User user = userRepository.findById(userId);

            if (mypageMemberUpdate.getNickname() != null) {
                user.setNickname(mypageMemberUpdate.getNickname());
            }

            if (mypageMemberUpdate.getPhone() != null) {
                user.setPhone(mypageMemberUpdate.getPhone());
            }

            if (mypageMemberUpdate.getBirthday() != null) {
                user.setBirthday(mypageMemberUpdate.getBirthday());
            }

            MypageMember returnmypagemember = MypageMember.builder()
                    .gender(user.getGender())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .phone(user.getPhone())
                    .birthday(user.getBirthday()).build();

            return returnmypagemember;
        }
        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    @Transactional
    public void memberCancel(Long userId)  throws BaseException{
        try{
            User user = userRepository.findById(userId);


            /*
            String provider = user.getProvider();
            String providerId = user.getProviderId();

            if(provider.equals("kakao")){

            }else if(provider.equals("google")){

            }else if(provider.equals("naver")){

            }else if(provider.equals("facebook")){

            }
            */

            userRepository.delete(user);
        } catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }


    }

    @Transactional
    public User saveNicknameAboutMe(Long userId,String nickname, String aboutMe){
        User user = userRepository.findById(userId);

        if(nickname != null) {
            user.setNickname(nickname);
        }

        if(aboutMe != null){
            user.setAboutMe(aboutMe);
        }

        return user;
    }



}
