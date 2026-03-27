package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.policy.ReservationPolicy;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.exception.AccommodationException;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.repository.AccommodationRepository;
import com.psh.bookflow.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;

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
        return accommodationRepository.findAllWithImagesAndAmenities().stream()
                .map(this::toResponse)
                .toList();
    }

    // 숙소 개별(ID) 조회
    @Transactional(readOnly = true)
    public AccommodationResponse findById(Long id) {
        Accommodation accommodation = getAccommodation(id);
        return toResponse(accommodation);
    }

    // "private" 숙소 개별 조회
    private Accommodation getAccommodation(Long id) {
        return accommodationRepository.findByIdWithImagesAndAmenities(id)
                .orElseThrow(() ->
                        new AccommodationException(ErrorCode.ACCOMMODATION_NOT_FOUND)
                );
    }


    // 검색 조건(날짜, 인원)에 따른 숙소 조회
    public List<AccommodationResponse> searchAvailable(LocalDate checkIn, LocalDate checkOut, int guest) {
        List<AccommodationResponse> accommodations = accommodationRepository.findAvailableAccommodations(
                        ReservationPolicy.ACTIVE_STATUSES,
                        checkIn,
                        checkOut,
                        guest
                ).stream()
                .map(this::toResponse)
                .toList();
        return  accommodations;
    }

    // accommodation -> accommodationResponse
    private AccommodationResponse toResponse(Accommodation accommodation) {
        Integer minPrice = this.roomRepository.findMinPriceByAccommodationId(accommodation.getId());
        return new AccommodationResponse(accommodation, minPrice);
    }
}