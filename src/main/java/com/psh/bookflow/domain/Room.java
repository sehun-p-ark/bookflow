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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //객실 고유 번호
    private Long id;

    // 객실 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 객실 설명
    @Column(columnDefinition = "TEXT")
    private String description;

    // 1박 가격
    @Column(nullable = false)
    private Long pricePerNight;

    // 최대 인원
    @Column(nullable = false)
    private Integer capacity;

    // 객실 상태 (AVAILABLE, UNAVAILABLE, MAINTENANCE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    // 숙소(FK) - 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}