package com.psh.bookflow.controller;

import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.dto.room.RoomResponse;
import com.psh.bookflow.service.AccommodationService;
import com.psh.bookflow.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final RoomService roomService;

    // 숙소등록 이거 아직 User 인증 때문에 못만드는 중 ㅠㅠ
//    @PostMapping
//    public ResponseEntity<AccommodationResponse> create(
//            @RequestBody AccommodationRequest request
//    ) {
//        AccommodationResponse response = accommodationService.create(request);
//
//        return ResponseEntity.ok(response);
//    }

    // 숙소 전체 조회
    @GetMapping
    public List<AccommodationResponse> getAll() {
        return accommodationService.findAll();
    }

    // 숙소 단일 조회
    @GetMapping("/{id}")
    public AccommodationResponse getById(@PathVariable Long id) {
        return accommodationService.findResponseById(id);
    }

    // 숙소별 객실 전체 조회
    @GetMapping("/{id}/rooms")
    public List<RoomResponse> getRooms(@PathVariable Long id) {
        return roomService.findByAccommodation(id);
    }

    // 특정 기간 중 예약 가능한 객실 조회
    @GetMapping("/{id}/rooms/available")
    public List<RoomResponse> getAvailableRooms(
            @PathVariable Long id,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut
    ) {
        return roomService.findAvailableRooms(id, checkIn, checkOut);
    }
}
