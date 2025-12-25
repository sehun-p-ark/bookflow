package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository
        // Accommodaion 엔티티를 Long 타입으로 JpaRepository가 관리한다.
        extends JpaRepository<Accommodation, Long> {
}
