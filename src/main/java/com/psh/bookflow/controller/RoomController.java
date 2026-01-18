package com.psh.bookflow.controller;

import com.psh.bookflow.dto.reservation.ReservationResponse;
import com.psh.bookflow.dto.room.RoomRequest;
import com.psh.bookflow.dto.room.RoomResponse;
import com.psh.bookflow.service.ReservationService;
import com.psh.bookflow.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final ReservationService reservationService;

    // 객실 등록 (로그인 여부는 Interceptor에서 처리됨)
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @RequestBody RoomRequest request
    ) {
        RoomResponse response = roomService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 객실 단일 조회 (로그인 불필요)
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        RoomResponse response = roomService.findResponseById(id);
        return ResponseEntity.ok(response);
    }

    // 객실 예약 여부 조회 (로그인 불필요) // 관리자용
    @GetMapping("/{id}/reservations")
    public List<ReservationResponse> getReservationsByRoom(
            @PathVariable("id") Long roomId
    ) {
        return reservationService.findByRoom(roomId);
    }
}
