package com.psh.bookflow.repository;

import com.psh.bookflow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 검색이 필요하므로 하나만 추가
    Optional<User> findByEmail(String email);
}
