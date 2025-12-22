package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public List<Accommodation> findAll() {
        return accommodationRepository.findAll();
    }

    public Accommodation findById(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
    }

    @Transactional
    public Accommodation save(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }
}
