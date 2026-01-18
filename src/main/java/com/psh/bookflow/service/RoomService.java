package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import com.psh.bookflow.domain.Room;
import com.psh.bookflow.dto.room.RoomRequest;
import com.psh.bookflow.dto.room.RoomResponse;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.RoomException;
import com.psh.bookflow.repository.AccommodationRepository;
import com.psh.bookflow.repository.ReservationRepository;
import com.psh.bookflow.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;

    private static final EnumSet<ReservationStatus> ACTIVE_STATUSES =
            EnumSet.of(ReservationStatus.REQUESTED, ReservationStatus.CONFIRMED);


    // 객실 등록
    @Transactional
    public RoomResponse create(RoomRequest request) {

        // 소속 숙소(Entity) 조회
        Accommodation accommodation =
                accommodationRepository.findById(request.getAccommodationId())
                        .orElseThrow(() ->
                                new RoomException(ErrorCode.ACCOMMODATION_NOT_FOUND)
                        );

        Room room = new Room(request.getName(), request.getDescription(), request.getPrice(), request.getCapacity(), accommodation);

        Room saved = roomRepository.save(room);
        return new RoomResponse(saved);
    }

    // 객실 조회
    @Transactional(readOnly = true)
    public RoomResponse findResponseById(Long roomId) {
        return new RoomResponse(getRoom(roomId));
    }

    // 숙소별 객실 목록 조회
    @Transactional(readOnly = true)
    public List<RoomResponse> findByAccommodation(Long accommodationId) {
        return roomRepository.findByAccommodationId(accommodationId).stream()
                .map(RoomResponse::new)
                .toList();
    }

    // 특정 기간에 예약 가능한 객실 조회
    public List<RoomResponse> findAvailableRooms(Long accommodationId, LocalDate checkIn, LocalDate checkOut, int guest) {

        List<Room> availableRooms = roomRepository.findAvailableRooms(accommodationId, ACTIVE_STATUSES, checkIn, checkOut, guest);

        return availableRooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
    }

    // "private" 객실 전체 조회
    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RoomException(ErrorCode.ROOM_NOT_FOUND)
                );
    }
}
