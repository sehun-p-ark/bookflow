package com.psh.bookflow.controller;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.user.LoginRequest;
import com.psh.bookflow.dto.user.SignupRequest;
import com.psh.bookflow.dto.user.UserResponse;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.UserException;
import com.psh.bookflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/")
    public ResponseEntity<UserResponse> register(@RequestBody SignupRequest request) {
        User user = userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getBirth(),
                request.getPhone()
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
        return ResponseEntity.noContent().build();
    }

    // 이메일로 회원 조회 (query param)
//    @GetMapping
//    public ResponseEntity<UserResponse> getUserByEmail(
//            @RequestParam String email,
//            @RequestAttribute("LOGIN_USER_ID") Long userId
//    ) {
//        User user = userService.getByEmail(email);
//        return ResponseEntity.ok(new UserResponse(user));
//    }


    // ID로 회원 조회 (query param)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");
        if (userId == null) {
            throw new UserException(ErrorCode.UNAUTHORIZED);
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(new UserResponse(user));
    }
}

