package com.psh.bookflow.service;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation reserve(Reservation reservation) {
        // 👉 여기에 나중에 핵심 비즈니스 규칙이 들어감
        // 예: 중복 예약 체크, 날짜 검증 등
        return reservationRepository.save(reservation);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    }
}
