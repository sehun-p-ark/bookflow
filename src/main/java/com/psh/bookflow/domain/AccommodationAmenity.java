package com.psh.bookflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AccommodationAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false, length = 50)
    private String amenity;

    public AccommodationAmenity(Accommodation accommodation, String amenity) {
        this.accommodation = accommodation;
        this.amenity = amenity;
    }
}