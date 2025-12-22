package com.psh.bookflow.dto.accommodation;

import com.psh.bookflow.domain.Accommodation;
import lombok.Getter;

@Getter
public class AccommodationResponse {

    private Long id;
    private String name;
    private String address;
    private String category;

    public AccommodationResponse(Accommodation accommodation) {
        this.id = accommodation.getId();
        this.name = accommodation.getName();
        this.address = accommodation.getAddress();
        this.category = accommodation.getCategory();
    }
}
