package umc.animore.config.auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;


// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login요청이 오면 자동으로 UserDetailService 타입으로 loc되어 있는 loadUserByUsername함수가 실행

// ----------jwt 적용 후-------------------
// 이 service가 localhost:8080/logn을 낚아채서 동작을 안한다. Security Config에서 form Login을 disable()했기 때문에!
// 따라서 낚아챌수 있게 만들기 위해 JwtAuthenticFilter를 만들어서 Security Config에 적용!
@Service
public class PrincipalDetailsService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;



    // 시큐리티 session = Authentication = UserDetails
    // 시큐리티 session(내부 Authentication(내부 UserDetails)) 알아서 착착 진행된다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user != null){
            return new PrincipalDetails(user);
        }
        return null;
    }
}
