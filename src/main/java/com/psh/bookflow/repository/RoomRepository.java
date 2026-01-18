package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.Room;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // 숙소에 있는 모든 객실 조회
    List<Room> findByAccommodationId(Long accommodationId);

    // 예약이 불가능한 (REQUESTED,CONFIRMED) 상태인 것 제외하고 조회
    List<Room> findByAccommodationIdAndIdNotIn(
            Long accommodationId,
            List<Long> excludedRoomIds
    );

    // 숙소에 있는 방 중 가장 작은 가격 찾아내기
    @Query("""
        SELECT MIN(r.price)
        FROM Room r
        WHERE r.accommodation.id = :accommodationId
    """)
    Integer findMinPriceByAccommodationId(Long accommodationId);


    // 검색 조건(날짜, 인원)에 맞는 방 찾기
    @Query("""
        select r
        from Room r
        where r.accommodation.id = :accommodationId
            and r.capacity >= :guest
            and not exists (
              select 1
              from Reservation res
              where res.room = r
                and res.status in :statuses
                and res.checkInDate < :checkOut
                and res.checkOutDate > :checkIn
          )
    """)
    List<Room> findAvailableRooms(
            @Param("accommodationId") Long accommodationId,
            @Param("statuses") EnumSet<ReservationStatus> statuses,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guest") int guest
    );
}
