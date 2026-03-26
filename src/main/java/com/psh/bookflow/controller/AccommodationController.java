package com.psh.bookflow.controller;

import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.dto.room.RoomResponse;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.ReservationException;
import com.psh.bookflow.service.AccommodationService;
import com.psh.bookflow.service.RoomService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
@Validated
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final RoomService roomService;

    // 숙소 전체 조회
    @GetMapping
    public List<AccommodationResponse> getAll() {
        return accommodationService.findAll();
    }

    // 검색 조건에 따른 숙소 조회
    @GetMapping("/search")
    public List<AccommodationResponse> search(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam @Min(1)int guest
    ) {
        validateSearchDates(checkIn, checkOut);
        return accommodationService.searchAvailable(checkIn, checkOut, guest);
    }

    // 숙소별 객실 전체 조회
    @GetMapping("/{id}/rooms")
    public List<RoomResponse> getRooms(@PathVariable Long id) {
        return roomService.findByAccommodation(id);
    }

    // 검색 조건에 따른 숙소 별 객실 조회
    @GetMapping("/{id}/rooms/search")
    public List<RoomResponse> getAvailableRooms(
            @PathVariable Long id,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam @Min(1)int guest
    ) {
        validateSearchDates(checkIn, checkOut);
        return roomService.findAvailableRooms(
                id,
                checkIn,
                checkOut,
                guest
        );
    }

    // 숙소 단일 조회 (필요한가???)
    @GetMapping("/{id}")
    public AccommodationResponse getById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    private static void validateSearchDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ReservationException(ErrorCode.RESERVATION_DATE_INVALID);
        }
    }
}
