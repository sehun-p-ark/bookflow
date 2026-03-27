package com.psh.bookflow.repository;

import com.psh.bookflow.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservationId(Long reservationId);

    Optional<Review> findByReservationId(Long reservationId);

    List<Review> findByAccommodationIdOrderByCreatedAtDesc(Long accommodationId);
}