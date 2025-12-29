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
                // 로그인/회원가입은 인증 제외
                .excludePathPatterns(
                        "/users",
                        "/users/login",
                        "/users/logout"
                )
                // 그 외 모든 요청은 로그인 필요
                .addPathPatterns("/**");
    }
}
