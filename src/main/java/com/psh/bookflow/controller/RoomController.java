package com.psh.bookflow.controller;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.Room;
import com.psh.bookflow.dto.room.RoomRequest;
import com.psh.bookflow.dto.room.RoomResponse;
import com.psh.bookflow.service.AccommodationService;
import com.psh.bookflow.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final AccommodationService accommodationService;

    /**
     * 객실 등록
     */
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @RequestBody RoomRequest request
    ) {
        // 소속 숙소 조회
        Accommodation accommodation =
                accommodationService.findById(request.getAccommodationId());

        // DTO → Entity
        Room room = new Room(
                request.getName(),
                request.getPrice(),
                request.getCapacity(),
                accommodation
        );

        Room saved = roomService.save(room);

        // Entity → Response DTO
        return ResponseEntity.ok(new RoomResponse(saved));
    }

    /**
     * 객실 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(
            @PathVariable Long id
    ) {
        Room room = roomService.findById(id);
        return ResponseEntity.ok(new RoomResponse(room));
    }
}
