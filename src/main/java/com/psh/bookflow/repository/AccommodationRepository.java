package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository
        // Accommodaion 엔티티를 Long 타입으로 JpaRepository가 관리한다.
        extends JpaRepository<Accommodation, Long> {

    // 숙소 목록 조회 + 이미지 함께 로딩 (N+1 방지)
    @Query("""
        select distinct a
        from Accommodation a
        left join fetch a.images
    """)
    List<Accommodation> findAllWithImages();

    // 숙소 단건 조회 + 이미지 함께 로딩
    @Query("""
        select a
        from Accommodation a
        left join fetch a.images
        where a.id = :id
    """)
    Optional<Accommodation> findByIdWithImages(Long id);

    // 조건(날짜, 인원)에 따른 예약 가능한 방이 있는 숙소 조회
    @Query("""
                select new com.psh.bookflow.dto.accommodation.AccommodationResponse(
                    a,
                    min(r.price)
                )
                from Accommodation a
                join a.rooms r
                where r.capacity >= :guest
                  and not exists (
                      select 1
                      from Reservation res
                      where res.room = r
                        and res.status in :statuses
                        and res.checkInDate < :checkOut
                        and res.checkOutDate > :checkIn
                  )
                     group by a
            """)
    List<AccommodationResponse> findAvailableAccommodations(
            @Param("statuses") EnumSet<ReservationStatus> statuses,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guest") int guest
    );
}
