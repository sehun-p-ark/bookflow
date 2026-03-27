package com.psh.bookflow.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    private String content;
}