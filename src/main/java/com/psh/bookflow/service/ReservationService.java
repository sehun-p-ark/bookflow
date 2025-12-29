package com.psh.bookflow.service;

import com.psh.bookflow.domain.*;
import com.psh.bookflow.dto.reservation.ReservationResponse;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.ReservationException;
import com.psh.bookflow.repository.ReservationRepository;
import com.psh.bookflow.repository.RoomRepository;
import com.psh.bookflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    // “예약이 살아있는 상태”만 겹침 검사 대상으로 잡음
    private static final EnumSet<ReservationStatus> ACTIVE_RESERVATION_STATUSES =
            EnumSet.of(ReservationStatus.REQUESTED, ReservationStatus.CONFIRMED);

    // 예약 생성하기
    @Transactional
    public Long create(Long userId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ReservationException(ErrorCode.USER_NOT_FOUND)
                );

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ReservationException(ErrorCode.ROOM_NOT_FOUND));

        // 객실 상태 체크(예약 가능)
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new ReservationException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        // 중복 예약(기간 겹침) 방지
        boolean overlapped = reservationRepository.existsOverlap(
                roomId,
                ACTIVE_RESERVATION_STATUSES,
                checkOut,   // checkOutDate
                checkIn     // checkInDate
        );


        if (overlapped) {
            throw new ReservationException(ErrorCode.RESERVATION_OVERLAPPED);
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // checkOut은 다음날이어야 하므로 1 이상
        long totalPrice = nights * room.getPrice();

        Reservation reservation = new Reservation(user, room, checkIn, checkOut, totalPrice);
        // 기본값: REQUESTED (엔티티에서 기본 세팅되어 있다는 전제)

        return reservationRepository.save(reservation).getId();
    }

    // 예약 확정 (요청 -> 확정)
    @Transactional
    public void confirm(Long reservationId) {
        Reservation reservation = getReservation(reservationId);

        if (reservation.getStatus() != ReservationStatus.REQUESTED) {
            throw new ReservationException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.confirm();
    }

    // 예약 취소 (요청/확정 -> 취소)
    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = getReservation(reservationId);

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            return;
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ReservationException(ErrorCode.RESERVATION_COMPLETED_CANNOT_CANCEL);
        }

        reservation.cancel();
    }

    // 예약 완료 (혹정 -> 완료)
    @Transactional
    public void complete(Long reservationId) {
        Reservation reservation = getReservation(reservationId);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.complete();
    }

    // 객실별 예약 상태 조회
    public List<ReservationResponse> findByRoom(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    // 유저별 예약 상태 조회
    public List<ReservationResponse> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // 예약 상태 전부 조회
    @Transactional(readOnly = true)
    public ReservationResponse findResponseById(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        // 새로운 ReservationResponse 객체로 만들어서 return
        return new ReservationResponse(reservation);
    }

    // 예약일자 검증 로직
    private static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new ReservationException(ErrorCode.RESERVATION_DATE_INVALID);
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ReservationException(ErrorCode.RESERVATION_DATE_INVALID);
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1) {
            throw new ReservationException(ErrorCode.RESERVATION_DATE_INVALID);
        }
    }

    // "private" findById 조회 로직
    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationException(ErrorCode.RESERVATION_NOT_FOUND)
                );
    }
}
