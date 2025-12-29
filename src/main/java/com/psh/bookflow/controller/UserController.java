package com.psh.bookflow.controller;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.user.LoginRequest;
import com.psh.bookflow.dto.user.UserRequest;
import com.psh.bookflow.dto.user.UserResponse;
import com.psh.bookflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        User user = userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );
        return ResponseEntity.ok(new UserResponse(user));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );
        session.setAttribute("LOGIN_USER_ID", user.getId());
        return ResponseEntity.ok(new UserResponse(user));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    // 이메일로 회원 조회 (query param)
    @GetMapping
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam String email
    ) {
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(new UserResponse(user));
    }
}

