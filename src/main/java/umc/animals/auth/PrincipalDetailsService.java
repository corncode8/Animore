package umc.animals.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.animals.Repository.UserRepository;
import umc.animals.model.User;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login요청이 오면 자동으로 UserDetailService 타입으로 loc되어 있는 loadUserByUsername함수가 실행
@Service
public class PrincipalDetailsService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    // 시큐리티 session = Authentication = UserDetails
    // 시큐리티 session(내부 Authentication(내부 UserDetails)) 알아서 착착 진행된다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null){
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
