package com.psh.bookflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 고유 번호
    private Long id;

    @Column(nullable = false, length = 100)
    // 숙소 이름
    private String name;

    // 숙소 상세 설명
    private String description;

    @Column(nullable = false, length = 255)
    // 숙소 주소
    private String address;

    @Column(length = 20)
    // 숙소 연락처
    private String phone;

    @Column(nullable = false, length = 30)
    // 숙소 종류 분류
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccommodationStatus status = AccommodationStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Accommodation(String name, String description, String address, String phone, String category, User owner) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.category = category;
        this.owner = owner;
    }
}
