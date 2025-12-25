package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.domain.ReservationStatus;
import com.psh.bookflow.dto.reservation.ReservationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 기간 중복 방지를 위한 쿼리
    @Query("""
        SELECT (count(r) > 0)
        FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status IN :statuses
          AND r.checkInDate < :checkOutDate
          AND r.checkOutDate > :checkInDate
    """)
    boolean existsOverlap(
            @Param("roomId") Long roomId,
            @Param("statuses") Collection<ReservationStatus> statuses,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("checkInDate") LocalDate checkInDate
    );

    // 객실 ID 기준 예약 확인
    List<ReservationResponse> findByRoomId(Long roomId);

    // 유저 ID 기준 예약 확인
    List<ReservationResponse> findByUserId(Long userId);

    // 겹치는 예약이 존재하는 roomId 목록 구하기
    @Query("""
        SELECT DISTINCT r.room.id
        FROM Reservation r
        WHERE r.room.accommodation.id = :accommodationId
          AND r.status IN :statuses
          AND r.checkInDate < :checkOutDate
          AND r.checkOutDate > :checkInDate
    """)
    List<Long> findReservedRoomIds(
            @Param("accommodationId") Long accommodationId,
            @Param("statuses") EnumSet<ReservationStatus> statuses,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("checkInDate") LocalDate checkInDate
    );
}
