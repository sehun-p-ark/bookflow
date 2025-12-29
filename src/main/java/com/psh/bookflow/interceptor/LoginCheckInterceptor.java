package com.psh.bookflow.interceptor;

import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        // 세션 가져오기 (없으면 null)
        HttpSession session = request.getSession(false);

        // 로그인 여부 판단
        if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
            // 로그인 안 된 상태 → 예외 발생
            throw new UserException(ErrorCode.UNAUTHORIZED);
        }

        // 로그인 된 상태 → 컨트롤러로 진행
        return true;
    }
}
