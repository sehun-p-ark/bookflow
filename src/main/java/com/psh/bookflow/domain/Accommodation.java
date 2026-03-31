package com.psh.bookflow.domain;

import com.psh.bookflow.domain.Statuses.AccommodationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 30)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccommodationStatus status = AccommodationStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(
            mappedBy = "accommodation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AccommodationImage> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "accommodation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(
            mappedBy = "accommodation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AccommodationAmenity> amenities = new ArrayList<>();

    public void deactivate() {
        this.status = AccommodationStatus.INACTIVE;
    }

    public void delete() {
        this.status = AccommodationStatus.DELETED;
    }

    public Accommodation(String name, String description, String address, String phone, String category, User owner) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.category = category;
        this.owner = owner;
    }

    public void updateInfo(String name, String description, String address, String phone, String category) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.category = category;
    }

    public void replaceImages(List<String> imageUrls) {
        this.images.clear();
        for (int i = 0; i < imageUrls.size(); i++) {
            this.images.add(new AccommodationImage(imageUrls.get(i), i, this));
        }
    }

}

