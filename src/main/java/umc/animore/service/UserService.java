package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * mypageMemberUpdate를 이용하여 user 업데이트
    **/
    @Transactional
    public void saveMypageMemberUpdate(MypageMemberUpdate mypageMemberUpdate,Long userId){

        User user = userRepository.findById(userId);

        user.setEmail(mypageMemberUpdate.getEmail());
        user.setPassword(mypageMemberUpdate.getPassword());
        user.setNickname(mypageMemberUpdate.getNickname());
        user.setPhone(mypageMemberUpdate.getPhone());
        user.setGender(mypageMemberUpdate.getGender());

    }

    @Transactional
    public void checkPassword(String password){


    }


}
