package com.psh.bookflow.controller;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.accommodation.AccommodationRequest;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.service.AccommodationService;
import com.psh.bookflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final UserService userService;

    /**
     * 숙소 등록
     */
    @PostMapping
    public ResponseEntity<AccommodationResponse> createAccommodation(
            @RequestBody AccommodationRequest request
    ) {
        // 🔥 임시 owner (나중에 로그인 사용자로 교체)
        User owner = userService.getByEmail("test@example.com");

        // DTO → Entity
        Accommodation accommodation = new Accommodation(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getPhone(),
                request.getCategory(),
                owner
        );

        Accommodation saved = accommodationService.save(accommodation);

        // Entity → Response DTO
        return ResponseEntity.ok(new AccommodationResponse(saved));
    }

    /**
     * 숙소 전체 조회
     */
    @GetMapping
    public ResponseEntity<List<AccommodationResponse>> getAll() {

        List<AccommodationResponse> responses =
                accommodationService.findAll().stream()
                        .map(AccommodationResponse::new)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 숙소 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> getById(
            @PathVariable Long id
    ) {
        Accommodation accommodation = accommodationService.findById(id);
        return ResponseEntity.ok(new AccommodationResponse(accommodation));
    }
}
