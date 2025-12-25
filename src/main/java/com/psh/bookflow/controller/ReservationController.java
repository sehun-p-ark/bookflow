package com.psh.bookflow.controller;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.dto.reservation.ReservationResponse;
import com.psh.bookflow.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestParam Long userId,
            @RequestParam Long roomId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut
    ) {
        Long reservationId =
                reservationService.create(userId, roomId, checkIn, checkOut);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationId);
    }

    // 예약 확정
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable("id") Long reservationId) {
        reservationService.confirm(reservationId);
        return ResponseEntity.ok().build();
    }

    // 예약 취소
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long reservationId) {
        reservationService.cancel(reservationId);
        return ResponseEntity.ok().build();
    }

    // 예약 완료
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable("id") Long reservationId) {
        reservationService.complete(reservationId);
        return ResponseEntity.ok().build();
    }

    // 예약 조회
    @GetMapping("/{id}")
    public ReservationResponse getReservationById(@PathVariable("id") Long reservationId) {
        return reservationService.findResponseById(reservationId);
    }
}
