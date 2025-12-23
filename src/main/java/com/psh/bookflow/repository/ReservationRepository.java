package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        select (count(r) > 0)
        from Reservation r
        where r.room.id = :roomId
          and r.status in :statuses
          and r.checkInDate < :checkOutDate
          and r.checkOutDate > :checkInDate
    """)
    boolean existsOverlap(
            @Param("roomId") Long roomId,
            @Param("statuses") Collection<ReservationStatus> statuses,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("checkInDate") LocalDate checkInDate
    );
}
