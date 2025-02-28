package com.example.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.example.user.service.TokenService;

@Configuration
public class AppConfig {

    @Bean
    public TokenService tokenService() {
        return new TokenService();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        
        // 필요한 헤더들 명시적으로 설정
        config.addExposedHeader("Authorization");
        config.addExposedHeader("X-Auth-User");
        config.addExposedHeader("AccessToken");
        config.addExposedHeader("RefreshToken");
        config.addExposedHeader("Content-Type");
        
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
