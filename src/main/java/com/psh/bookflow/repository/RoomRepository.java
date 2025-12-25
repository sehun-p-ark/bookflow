package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
