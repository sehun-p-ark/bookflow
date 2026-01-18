package com.psh.bookflow.controller;

import com.psh.bookflow.dto.reservation.ReservationRequest;
import com.psh.bookflow.dto.reservation.ReservationResponse;
import com.psh.bookflow.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody ReservationRequest request,
            @RequestAttribute("LOGIN_USER_ID") Long userId) {

        Long reservationId = reservationService.create(
                userId,
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationId);
    }

    // 예약 확정
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Boolean>> confirm(
            @PathVariable("id") Long reservationId,
            @RequestAttribute("LOGIN_USER_ID") Long userId) {
        reservationService.confirm(reservationId, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 예약 취소
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancel(
            @PathVariable("id") Long reservationId,
            @RequestAttribute("LOGIN_USER_ID") Long userId) {
        reservationService.cancel(reservationId, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 예약 완료
    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Boolean>> complete(
            @PathVariable("id") Long reservationId,
            @RequestAttribute("LOGIN_USER_ID") Long userId) {
        reservationService.complete(reservationId, userId);
        return ResponseEntity.ok(Map.of("success", true));

    }

    // 예약 조회
    @GetMapping("/me")
    public List<ReservationResponse> getMyReservations(
            @RequestAttribute("LOGIN_USER_ID") Long userId) {
        return reservationService.findByUser(userId);
    }
}
