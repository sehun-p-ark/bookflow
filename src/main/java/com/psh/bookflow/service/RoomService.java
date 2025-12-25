package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.ReservationStatus;
import com.psh.bookflow.domain.Room;
import com.psh.bookflow.dto.room.RoomRequest;
import com.psh.bookflow.dto.room.RoomResponse;
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
    private final ReservationRepository reservationRepository;

    private static final EnumSet<ReservationStatus> ACTIVE_STATUSES =
            EnumSet.of(ReservationStatus.REQUESTED, ReservationStatus.CONFIRMED);


    // 객실 등록
    @Transactional
    public RoomResponse create(RoomRequest request) {

        // 소속 숙소(Entity) 조회
        Accommodation accommodation =
                accommodationRepository.findById(request.getAccommodationId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("숙소를 찾을 수 없습니다. id=" + request.getAccommodationId())
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
    public List<RoomResponse> findAvailableRooms(Long accommodationId, LocalDate checkIn, LocalDate checkOut) {

        // 1) 해당 기간에 이미 예약된 roomId 목록
        List<Long> reservedRoomIds = reservationRepository.findReservedRoomIds(accommodationId, ACTIVE_STATUSES, checkOut, checkIn);

        // 2) 예약된 객실 제외하고 조회
        List<Room> availableRooms = reservedRoomIds.isEmpty()
                ? roomRepository.findByAccommodationId(accommodationId)
                : roomRepository.findByAccommodationIdAndIdNotIn(accommodationId, reservedRoomIds);

        return availableRooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
    }

    // "private" 객실 전체 조회
    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException("객실을 찾을 수 없습니다. id=" + roomId)
                );
    }
}
