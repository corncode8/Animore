package umc.animore.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   // 내서버가 응답할때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
        config.addAllowedOrigin("*");   // 모든 ip에 응답을 허용하겠다
        config.addAllowedHeader("*");   // 모든 header에 응답을 허용하겠다
        config.addAllowedMethod("*");   // 모든 post,get,put,delete,patch 요청을 허용하겠다
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/api/**",config);

        return new CorsFilter(source);
    }
}

