package com.psh.bookflow.service;

import com.psh.bookflow.domain.*;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import com.psh.bookflow.domain.Statuses.RoomStatus;
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
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회용 트랜잭션
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
        validateDates(checkIn, checkOut); // 날짜 검증

        User user = userRepository.findById(userId) // 유저 검증
                .orElseThrow(() ->
                        new ReservationException(ErrorCode.USER_NOT_FOUND)
                );

        Room room = roomRepository.findById(roomId) // 객실 검증
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


        if (overlapped) { //만약에 겹치면 예외 발생
            throw new ReservationException(ErrorCode.RESERVATION_OVERLAPPED);
        }

        // 숙박 일수 계산하기
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // checkOut은 다음날이어야 하므로 1 이상
        // 총 가격 계산하기
        long totalPrice = nights * room.getPrice();
        // 예약 엔티티 생성
        Reservation reservation = new Reservation(user, room, checkIn, checkOut, totalPrice);
        // 기본값: REQUESTED (엔티티에서 기본 세팅되어 있다는 전제)

        //DB에 저장
        return reservationRepository.save(reservation).getId();
    }

    // 예약 확정 (요청 -> 확정)
    @Transactional
    public void confirm(Long reservationId, Long loginUserId) {
        Reservation reservation = getReservation(reservationId); // 예약 조회하기

        if (!reservation.getUser().getId().equals(loginUserId)) { // 본인 예약인지 유저 확인
            throw new ReservationException(ErrorCode.FORBIDDEN);
        }

        if (reservation.getStatus() != ReservationStatus.REQUESTED) { // 요청 상태인지 확인하기
            throw new ReservationException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.confirm(); // 요청 > 확정 상태로 변경
    }

    // 예약 취소 (요청/확정 -> 취소)
    @Transactional
    public void cancel(Long reservationId, Long loginUserId) {
        Reservation reservation = getReservation(reservationId);

        if (!reservation.getUser().getId().equals(loginUserId)) {
            throw new ReservationException(ErrorCode.FORBIDDEN);
        }
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            return;
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ReservationException(ErrorCode.RESERVATION_COMPLETED_CANNOT_CANCEL);
        }

        reservation.cancel();
    }

    // 예약 완료 (확정 -> 완료)
    @Transactional
    public void complete(Long reservationId, Long loginUserId) {
        Reservation reservation = getReservation(reservationId);

        if (!reservation.getUser().getId().equals(loginUserId)) {
            throw new ReservationException(ErrorCode.FORBIDDEN);
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.complete();
    }

    // 객실별 예약 상태 조회
    public List<ReservationResponse> findByRoom(Long roomId) {
        return reservationRepository.findByRoomId(roomId)
                //Reservation 리스트들을 풀어헤친다.
                .stream()
                // 람다식임 (의미는 .map(reservation -> new ReservationResponse(reservation);
                .map(ReservationResponse::new)
                // 다시 List형태로 묶어서 DTO LIST로 전환 완료
                .toList();
    }

    // 유저별 예약 상태 조회
    public List<ReservationResponse> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(ReservationResponse::new)
                .toList();
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
        LocalDate today = LocalDate.now();
        if (checkIn == null || checkOut == null) {
            throw new ReservationException(ErrorCode.RESERVATION_DATE_INVALID);
        }
        if (checkIn.isBefore(today)) {
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
