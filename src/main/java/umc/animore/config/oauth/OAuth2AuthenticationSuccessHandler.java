package umc.animore.config.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import umc.animore.config.auth.PrincipalDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("OAuth2AuthenticationSuccessHandler 실행됨: 인증이완료되었따는 뜻임");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60*60*3600*30)))
                .withClaim("id",principalDetails.getUser().getId())
                .withClaim("username",principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));

        String targetUri = UriComponentsBuilder.fromUriString("/")
                .queryParam("token", jwtToken)
                .build().toUriString();

        response.addHeader("Authorization","Bearer " + jwtToken);
        getRedirectStrategy().sendRedirect(request,response,targetUri);
    }
}
