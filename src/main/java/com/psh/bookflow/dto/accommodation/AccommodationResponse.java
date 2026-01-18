package com.psh.bookflow.dto.accommodation;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.AccommodationImage;
import com.psh.bookflow.domain.Statuses.AccommodationStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
public class AccommodationResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String address;
    private final String phone;
    private final String category;
    private final AccommodationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<String> imageUrls;
    private final Integer minPrice;

    public AccommodationResponse(Accommodation accommodation, Integer minPrice) {
        this.id = accommodation.getId();
        this.name = accommodation.getName();
        this.description = accommodation.getDescription();
        this.address = accommodation.getAddress();
        this.phone = accommodation.getPhone();
        this.category = accommodation.getCategory();
        this.status = accommodation.getStatus();
        this.createdAt = accommodation.getCreatedAt();
        this.updatedAt = accommodation.getUpdatedAt();
        this.minPrice = minPrice;
        this.imageUrls = accommodation.getImages().stream()
                .sorted(Comparator.comparingInt(AccommodationImage::getSortOrder)) //.sorted(a-b)와 동일
                .map(AccommodationImage::getImageUrl) // getImage
                .toList();
    }
}
