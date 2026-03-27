package com.psh.bookflow.controller;

import com.psh.bookflow.dto.review.ReviewRequest;
import com.psh.bookflow.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Long> create(
            @RequestAttribute("LOGIN_USER_ID") Long userId,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long reviewId = reviewService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }
}