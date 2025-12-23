package com.psh.bookflow.controller;

import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.user.UserRequest;
import com.psh.bookflow.dto.user.UserResponse;
import com.psh.bookflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원 생성
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest request
    ) {
        // DTO → Entity 변환 (Controller 책임)
        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        User savedUser = userService.save(user);

        // Entity → Response DTO 변환
        return ResponseEntity.ok(new UserResponse(savedUser));
    }

    /**
     * 이메일로 회원 조회
     */
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email
    ) {
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(new UserResponse(user));
    }
}
