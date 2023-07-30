package umc.animore.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static umc.animore.config.exception.BaseResponseStatus.*;

// 시큐리티가 filter를 가지고 있는데 그 필터 중에 BasicAuthenticationFilter라는 것이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.
// 만약에 권한이 인증이 필요한 주소가 아니라면 이 필터를 안타요!
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을 떄 해당 필터를 타게됨.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader: " + jwtHeader);

        try {
            // header 가 있는지 확인
            if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
                chain.doFilter(request, response);
                return;
            }

            //JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
            String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

            System.out.print(jwtToken);

            String username =
                    JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString();

            System.out.print(username);

            // 서명이 정상적으로 됨
            if (username != null) {
                User userEntity = userRepository.findByUsername(username);

                PrincipalDetails principalDetails = new PrincipalDetails(userEntity);


                //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                // 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(TokenExpiredException e){
            try {
                throw new BaseException(GET_JWT_Expired);
            } catch (BaseException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch(SignatureVerificationException e){
            try {
                throw new BaseException(GET_JWT_InValid);
            } catch (BaseException ex) {
                throw new RuntimeException(ex);
            }
        }catch(JWTDecodeException e){
            try {
                throw new BaseException(GET_JWT_Decode_ERROR);
            } catch (BaseException ex) {
                throw new RuntimeException(ex);
            }
        }

        chain.doFilter(request,response);

    }
}
