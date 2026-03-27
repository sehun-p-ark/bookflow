package com.psh.bookflow.dto.review;

import com.psh.bookflow.domain.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private final Long id;
    private final Long reservationId;
    private final Long accommodationId;
    private final String accommodationName;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.reservationId = review.getReservation().getId();
        this.accommodationId = review.getAccommodation().getId();
        this.accommodationName = review.getAccommodation().getName();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}