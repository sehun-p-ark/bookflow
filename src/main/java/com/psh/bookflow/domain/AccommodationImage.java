package com.psh.bookflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AccommodationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    public AccommodationImage(String imageUrl, int sortOrder, Accommodation accommodation) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.accommodation = accommodation;
    }
}
