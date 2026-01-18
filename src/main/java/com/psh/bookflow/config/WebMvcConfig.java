package com.psh.bookflow.config;

import com.psh.bookflow.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                // 정적 리소스
                // 로그인 인증 불필요
                .excludePathPatterns(
                        "/frontend/**",
                        "/error",
                        "/favicon.ico"
                )
                // 로그인 인증 필요함
                .addPathPatterns(
                        "/api/users/me",
                        "/api/reservations/**"
                );
    }
}
