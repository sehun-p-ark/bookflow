package com.psh.bookflow.controller;

import com.psh.bookflow.dto.accommodation.AccommodationRequest;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
public class HostController {

    private final AccommodationService accommodationService;

    @GetMapping
    public List<AccommodationResponse> getMyAccommodations(
            @RequestAttribute("LOGIN_USER_ID") Long userId
    ) {
        return accommodationService.findByOwner(userId);
    }

    @PostMapping
    public AccommodationResponse create(
            @RequestAttribute("LOGIN_USER_ID") Long userId,
            @RequestBody AccommodationRequest request
    ) {
        return accommodationService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public AccommodationResponse update(
            @PathVariable Long id,
            @RequestAttribute("LOGIN_USER_ID") Long userId,
            @RequestBody AccommodationRequest request
    ) {
        return accommodationService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestAttribute("LOGIN_USER_ID") Long userId
    ) {
        accommodationService.delete(userId, id);
    }
}