package com.psh.bookflow.dto.accommodation;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.AccommodationStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class AccommodationResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String address;
    private final String phone;
    private final String category;
    private final AccommodationStatus status;
    private final Long ownerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AccommodationResponse(Accommodation accommodation) {
        this.id = accommodation.getId();
        this.name = accommodation.getName();
        this.description = accommodation.getDescription();
        this.address = accommodation.getAddress();
        this.phone = accommodation.getPhone();
        this.category = accommodation.getCategory();
        this.status = accommodation.getStatus();
        this.ownerId = accommodation.getOwner().getId();
        this.createdAt = accommodation.getCreatedAt();
        this.updatedAt = accommodation.getUpdatedAt();
    }
}
