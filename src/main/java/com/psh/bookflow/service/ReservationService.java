package com.psh.bookflow.service;

import com.psh.bookflow.domain.*;
import com.psh.bookflow.repository.ReservationRepository;
import com.psh.bookflow.repository.RoomRepository;
import com.psh.bookflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    // “예약이 살아있는 상태”만 겹침 검사 대상으로 잡음
    private static final EnumSet<ReservationStatus> ACTIVE_RESERVATION_STATUSES =
            EnumSet.of(ReservationStatus.REQUESTED, ReservationStatus.CONFIRMED);

    /**
     * 예약 생성
     */
    @Transactional
    public Long create(Long userId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomId=" + roomId));

        // 객실 상태 체크(예약 가능)
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("예약 불가능한 객실 상태입니다. status=" + room.getStatus());
        }

        // 중복 예약(기간 겹침) 방지
        boolean overlapped = reservationRepository.existsOverlap(
                roomId,
                ACTIVE_RESERVATION_STATUSES,
                checkOut,   // checkOutDate
                checkIn     // checkInDate
        );


        if (overlapped) {
            throw new IllegalStateException("해당 기간에 이미 예약이 존재합니다.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // checkOut은 다음날이어야 하므로 1 이상
        long totalPrice = nights * (long) room.getPrice();

        Reservation reservation = new Reservation(user, room, checkIn, checkOut, totalPrice);
        // 기본값: REQUESTED (엔티티에서 기본 세팅되어 있다는 전제)

        return reservationRepository.save(reservation).getId();
    }

    /**
     * 예약 확정(요청 -> 확정)
     */
    @Transactional
    public void confirm(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. id=" + reservationId));

        if (reservation.getStatus() != ReservationStatus.REQUESTED) {
            throw new IllegalStateException("요청(REQUESTED) 상태에서만 확정할 수 있습니다. status=" + reservation.getStatus());
        }

        reservation.confirm();
    }

    /**
     * 예약 취소(요청/확정 -> 취소)
     */
    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. id=" + reservationId));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            return; // 이미 취소면 멱등 처리
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("완료(COMPLETED)된 예약은 취소할 수 없습니다.");
        }

        reservation.cancel();
    }

    /**
     * 예약 완료(확정 -> 완료)
     */
    @Transactional
    public void complete(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. id=" + reservationId));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정(CONFIRMED) 상태에서만 완료 처리할 수 있습니다. status=" + reservation.getStatus());
        }

        reservation.complete();
    }

    private static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("체크인/체크아웃 날짜는 필수입니다.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("체크아웃은 체크인 이후여야 합니다.");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1) {
            throw new IllegalArgumentException("최소 1박 이상이어야 합니다.");
        }
    }
}
