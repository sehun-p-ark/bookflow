package com.psh.bookflow.config;

import com.psh.bookflow.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                // 정적 리소스
                // 로그인 인증 불필요
                .excludePathPatterns(
                        "/frontend/**",
                        "/uploads/**",
                        "/error",
                        "/favicon.png"
                )
                // 로그인 인증 필요함
                .addPathPatterns(
                        "/api/users/me",
                        "/api/users/me/**",
                        "/api/host/**",
                        "/api/files/**",
                        "/api/rooms",
                        "/api/reservations",
                        "/api/reviews",
                        "/api/reviews/**",
                        "/api/reservations/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
