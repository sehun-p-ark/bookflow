package com.psh.bookflow.service;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.domain.Review;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.review.ReviewRequest;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.exception.ReservationException;
import com.psh.bookflow.exception.UserException;
import com.psh.bookflow.repository.ReservationRepository;
import com.psh.bookflow.repository.ReviewRepository;
import com.psh.bookflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(Long loginUserId, ReviewRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(loginUserId)) {
            throw new ReservationException(ErrorCode.FORBIDDEN);
        }

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new ReservationException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new ReservationException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Review review = new Review(
                reservation,
                user,
                reservation.getRoom().getAccommodation(),
                request.getRating(),
                request.getContent().trim()
        );

        return reviewRepository.save(review).getId();
    }
}