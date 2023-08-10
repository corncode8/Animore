package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.exception.BaseException;
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
    public MypageMemberUpdate saveMypageMemberUpdate(MypageMemberUpdate mypageMemberUpdate, Long userId) throws BaseException {

        try {
            if (mypageMemberUpdate.getNickname() == null) {
                throw new BaseException(GET_USER_EMPTY_NICKNAME_NAME);
            }


            User user = userRepository.findById(userId);

            user.setEmail(mypageMemberUpdate.getEmail());
            user.setPassword(mypageMemberUpdate.getPassword());
            user.setNickname(mypageMemberUpdate.getNickname());
            user.setPhone(mypageMemberUpdate.getPhone());
            user.setGender(mypageMemberUpdate.getGender());
            user.setBirthday(mypageMemberUpdate.getBirthday());

            return mypageMemberUpdate;
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

        user.setNickname(nickname);
        user.setAboutMe(aboutMe);

        return user;
    }



}
