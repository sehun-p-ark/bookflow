package com.psh.bookflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity //현재 클래스를 DB 테이블로 쓰겠음
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 (JPA 엔티티는 필수)
public class User { // user 테이블

    // PK(Primary Key) 지정
    @Id
    // PK 값을 DB에서 자동 증가(auto increment) 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 이름
    // 컬럼 설정 null 불가, varchar(30)
    @Column(nullable = false, length = 30)
    private String name;

    // 이메일 (중복 불가)
    // (null 불가, 중복 불가, varchar(50))
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    // 패스워드
    @Column(nullable = false)
    private String password;

    // 생성일
    @Column(updatable = false) // 수정(update)될 때 변경되면 안됨
    // PrePersist에서 추가함
    private LocalDateTime createdAt;

    // 수정일
    // Column을 사용하지 않으면 null값이 들어가게 됨 (열 자체는 추가됨)
    // PrePersist에서 추가함으로 null값 안들어감
    private LocalDateTime updatedAt;

    // JPA 라이프사이클 어노테이션,
    // 엔티티가 DB 저장되기 직전 (User 객체가 처음 save())될 때 실행됨
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // 엔티티가 UPDATE 되기 직전 실행됨
    public void onUpdate() {
        // 수정일이 자동 갱신
        this.updatedAt = LocalDateTime.now();
    }
}
