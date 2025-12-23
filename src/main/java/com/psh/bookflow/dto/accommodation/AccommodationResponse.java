package com.psh.bookflow.dto.accommodation;

import com.psh.bookflow.domain.Accommodation;
import lombok.Getter;

@Getter
public class AccommodationResponse {

    private final Long id;
    private final String name;
    private final String address;
    private final String category;

    public AccommodationResponse(Accommodation accommodation) {
        this.id = accommodation.getId();
        this.name = accommodation.getName();
        this.address = accommodation.getAddress();
        this.category = accommodation.getCategory();
    }
}
