package com.psh.bookflow.controller;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.domain.Room;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.reservation.ReservationRequest;
import com.psh.bookflow.dto.reservation.ReservationResponse;
import com.psh.bookflow.service.ReservationService;
import com.psh.bookflow.service.RoomService;
import com.psh.bookflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final RoomService roomService;

    /**
     * 예약 생성
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request
    ) {
        // 예약자 조회
        User user = userService.getById(request.getUserId());

        // 객실 조회
        Room room = roomService.findById(request.getRoomId());

        // DTO → Entity
        Reservation reservation = new Reservation(
                user,
                room,
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        Reservation saved = reservationService.reserve(reservation);

        // Entity → Response DTO
        return ResponseEntity.ok(new ReservationResponse(saved));
    }

    /**
     * 예약 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable Long id
    ) {
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }
}
