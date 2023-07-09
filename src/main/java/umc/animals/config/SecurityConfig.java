package umc.animals.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.*;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableMethodSecurity(securedEnabled = true,prePostEnabled = true)        //secure 어노테이션 활성화 ,preAuthorize어노테이션 활성화
public class SecurityConfig {



    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable());
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/user/**").authenticated() //인증만 되면 들어갈 수 있다!
                        .requestMatchers("/manager/**").access(hasAnyRole("MANAGER","ADMIN"))
                        .requestMatchers("/admin/**").access(hasRole("ADMIN"))
                        .anyRequest().permitAll()
                ).formLogin(form -> form.loginPage("/loginForm"));

        http.formLogin(form -> form.loginProcessingUrl("/login")); // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
        http.formLogin(form -> form.defaultSuccessUrl("/")); //  /loginForm에서 login을 하면 "/" 위치로 보내주고 특정페이지에서 로그인하면 그 특정페이지로 리턴시켜줄게! -> 너무좋다!


        http.oauth2Login(oauth -> oauth.loginPage("/loginForm")) //카카오 로그인이 완료된 뒤의 후처리가 필요함
                .oauth2Login(oauth->oauth.defaultSuccessUrl("/"))
                .oauth2Login(oauth->oauth.failureUrl("/"))
                .oauth2Login(oauth-> oauth.userInfoEndpoint().userService(dfdfd)); //이부분모르겠다


        return http.build();


    }

}
