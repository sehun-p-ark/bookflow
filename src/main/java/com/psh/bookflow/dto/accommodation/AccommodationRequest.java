package com.psh.bookflow.dto.accommodation;

import lombok.Getter;

import java.util.List;

@Getter
public class AccommodationRequest {
    private String name;
    private String description;
    private String address;
    private String phone;
    private String category;
    private List<String> imageUrls;
}
