package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.dto.accommodation.AccommodationRequest;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final UserService userService;

    // 숙소 등록
//    @Transactional
//    public AccommodationResponse create(AccommodationRequest request) {
//
//        // 🔥 임시 사용자 (나중에 로그인 사용자로 교체)
//        User owner = userService.getByEmail("test@example.com");
//
//        Accommodation accommodation = new Accommodation(
//                request.getName(),
//                request.getDescription(),
//                request.getAddress(),
//                request.getPhone(),
//                request.getCategory(),
//                owner
//        );
//
//        Accommodation saved = accommodationRepository.save(accommodation);
//        return new AccommodationResponse(saved);
//    }

    // 숙소 전체 조회
    @Transactional(readOnly = true)
    public List<AccommodationResponse> findAll() {
        return accommodationRepository.findAll().stream()
                .map(AccommodationResponse::new)
                .toList();
    }

    // 숙소 개별(ID) 조회
    @Transactional(readOnly = true)
    public AccommodationResponse findResponseById(Long id) {
        Accommodation accommodation = getAccommodation(id);
        // 새로운 AccommodationResponse 객체 생성 후 return
        return new AccommodationResponse(accommodation);
    }

    // "private" 숙소 개별 조회
    private Accommodation getAccommodation(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("숙소를 찾을 수 없습니다. id=" + id)
                );
    }


}
