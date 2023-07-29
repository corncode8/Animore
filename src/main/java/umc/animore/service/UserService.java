package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.exception.BaseException;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static umc.animore.config.exception.BaseResponseStatus.GET_USER_EMPTY_NICKNAME_NAME;
import static umc.animore.config.exception.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetService petService;

    public User getUserId(Long userId) {
        return userRepository.findById(userId);
    }

    // 예약상세 저장내용 불러오기
    // 유저정보 + 펫정보 불러오기
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
        userinfoMap.put("반려동물 이름", pet.getPetName());
        userinfoMap.put("반려동물 종류", pet.getPetType());
        userinfoMap.put("반려동물 성별", pet.getPetGender());
        userinfoMap.put("유저 이름", user.getUsername());
        userinfoMap.put("전화번호", user.getPhone());
        userinfoMap.put("주소", user.getAddress());
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

            return mypageMemberUpdate;
        }
        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

}
