package com.psh.bookflow.service;

import com.psh.bookflow.domain.User;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.UserException;
import com.psh.bookflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public User register(String email, String rawPassword, String name, LocalDate birth, String phone) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_EMAIL_DUPLICATED);
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);
        // User 생성 (암호화된 비밀번호 사용)
        User user = new User(email, encodedPassword, name, birth, phone);

        return userRepository.save(user);
    }

    // 로그인
    public User login(String email, String rawPassword) {
        // 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserException(ErrorCode.USER_NOT_FOUND)
                );
        // 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserException(ErrorCode.USER_PASSWORD_MISMATCH);
        }
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    // Email로 회원 찾기
//    public User getByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() ->
//                        new UserException(ErrorCode.USER_NOT_FOUND)
//                );
//    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserException(ErrorCode.USER_NOT_FOUND)
                );
    }


    @Transactional
    public User updateName(Long userId, String name) {
        User user = getById(userId);
        user.updateName(name);
        return user;
    }

    @Transactional
    public void updatePassword(Long userId, String currentRawPassword, String newRawPassword) {
        User user = getById(userId);

        if (!passwordEncoder.matches(currentRawPassword, user.getPassword())) {
            throw new UserException(ErrorCode.USER_PASSWORD_MISMATCH);
        }

        if (passwordEncoder.matches(newRawPassword, user.getPassword())) {
            throw new UserException(ErrorCode.USER_PASSWORD_SAME_AS_OLD);
        }

        user.updatePassword(passwordEncoder.encode(newRawPassword));
    }
}
