package umc.animore.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

//스프링시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// /login 요청해서 username, password 전송하면(pst)
// UsernamePasswordAuthenticationFilter 동작을 함.


@RequiredArgsConstructor
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;




    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtUsernamePasswordAuthenticationFilter : 진입");

        // 1.username, password 받아서

        ObjectMapper om = new ObjectMapper();
        try {
            User user = om.readValue(request.getInputStream(),User.class);

            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());

            // authenticationamanger에 토큰을 넣어서 던지면
            // PrincipalDetailsService의 loadUserByUsername()함수가 실행되어
            // 알아서 인증을해준다.
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            //authentication 객체가 session영역에 저장됨 => 로그인이 되었다는 뜻!
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println(principalDetails.getUser().getUsername());

            return authentication;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 2.정상인지 로그인 시도를 해본다. authenticationManager로 로그인 시도를 하면 !!
        //   PrincipalDetailService가 호출된다. loadUserByUsername()함수가 실행됨.
        // 3. PrincipalDetails를 세션에 담고(권한 관리를 위해서)
        // 4. JWT토큰을 만들어서 응답을해주면된다.


    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다!
    //JWT 토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이완료되었따는 뜻임");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("id",principalDetails.getUser().getId())
                .withClaim("username",principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));

        response.addHeader("Authorization","Bearer " + jwtToken);



    }




    @Override
    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }


}