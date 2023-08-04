package umc.animore.config;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import umc.animore.config.jwt.JwtUsernamePasswordAuthenticationFilter;
import umc.animore.config.jwt.JwtAuthorizationFilter;
import umc.animore.config.oauth.OAuth2AuthenticationSuccessHandler;
import umc.animore.config.oauth.PrincipalOauth2UserService;
import umc.animore.filter.ExceptionHandlerFilter;
import umc.animore.repository.UserRepository;


@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private CorsConfig corsConfig;

    private final PrincipalOauth2UserService principalOauth2UserService;

    private final CorsFilter corsFilter;

    private final UserRepository userRepository;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;



    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository))
                    .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthorizationFilter.class);;
        }
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 쓰지 않겠다
                        .and()
                        //.addFilter(corsFilter)   // CrossOrgin문제를 인증이필요한 주소든 아니든 전부다 해결해준다  /  @CrossOrgin은 인증(X)만 해결해준다
                        .formLogin().disable()   // formLogin을 쓰지 않겠다.
                        .httpBasic().disable()   //http 기본인증방식을 쓰지않겠다 (basic방식 : id/pw를 https주소를 이요해서 암호화해서 보내줌  // 우리는 Bearer방식으로 Token(jwt)를 쓸꺼니깐!
                        .apply(new MyCustomDsl());


        http.authorizeRequests()
                .antMatchers("/api/v1/user/**").access("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
                .antMatchers("/api/v1/manager/**").access("hasRole('MANAGER') or hasRole('ADMIN')")
                .antMatchers("/api/v1/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('MANAGER') or hasRole('ADMIN')")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/manger/**").hasRole("MANAGER")
                .anyRequest().permitAll()
                .and()					//추가
                .oauth2Login()				// OAuth2기반의 로그인인 경우
                .loginPage("/loginForm")		// 인증이 필요한 URL에 접근하면 /loginForm으로 이동
                .defaultSuccessUrl("/")			// 로그인 성공하면 "/" 으로 이동
                .failureUrl("/loginForm")		// 로그인 실패 시 /loginForm으로 이동
                .userInfoEndpoint()			// 로그인 성공 후 사용자정보를 가져온다
                .userService(principalOauth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler);	//사용자정보를 처리할 때 사용한다

        return http.build();
    }
}