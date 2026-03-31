package com.psh.bookflow.service;

import com.psh.bookflow.domain.Accommodation;
import com.psh.bookflow.domain.User;
import com.psh.bookflow.domain.policy.ReservationPolicy;
import com.psh.bookflow.dto.accommodation.AccommodationRequest;
import com.psh.bookflow.dto.accommodation.AccommodationResponse;
import com.psh.bookflow.exception.AccommodationException;
import com.psh.bookflow.exception.ErrorCode;
import com.psh.bookflow.repository.AccommodationRepository;
import com.psh.bookflow.repository.RoomRepository;
import com.psh.bookflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("hotel", "motel", "pension", "resort", "etc");


    @Transactional
    public AccommodationResponse create(Long ownerId, AccommodationRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new AccommodationException(ErrorCode.USER_NOT_FOUND));

        Accommodation accommodation = new Accommodation(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getPhone(),
                normalizeCategory(request.getCategory()),
                owner
        );
        validateImageUrls(request.getImageUrls());
        accommodation.replaceImages(request.getImageUrls());

        Accommodation saved = accommodationRepository.save(accommodation);
        return toResponse(saved);
    }

    // 숙소 전체 조회
    @Transactional(readOnly = true)
    public List<AccommodationResponse> findAll() {
        return accommodationRepository.findAllWithImagesAndAmenities().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AccommodationResponse> findByOwner(Long ownerId) {
        return accommodationRepository.findByOwnerIdWithImagesAndAmenities(ownerId).stream()
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

    @Transactional
    public AccommodationResponse update(Long ownerId, Long accommodationId, AccommodationRequest request) {
        Accommodation accommodation = getAccommodation(accommodationId);
        validateOwner(ownerId, accommodation);

        accommodation.updateInfo(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getPhone(),
                normalizeCategory(request.getCategory())
        );
        validateImageUrls(request.getImageUrls());
        accommodation.replaceImages(request.getImageUrls());
        return toResponse(accommodation);
    }

    @Transactional
    public void delete(Long ownerId, Long accommodationId) {
        Accommodation accommodation = getAccommodation(accommodationId);
        validateOwner(ownerId, accommodation);
        accommodation.delete();
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

    private static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new AccommodationException(ErrorCode.ACCOMMODATION_CATEGORY_INVALID);
        }

        String normalized = category.toLowerCase();
        if (!ALLOWED_CATEGORIES.contains(normalized)) {
            throw new AccommodationException(ErrorCode.ACCOMMODATION_CATEGORY_INVALID);
        }
        return normalized;
    }

    private static void validateImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty() || imageUrls.stream().anyMatch(url -> url == null || url.isBlank())) {
            throw new AccommodationException(ErrorCode.ACCOMMODATION_IMAGE_REQUIRED);
        }
    }

    private static void validateOwner(Long ownerId, Accommodation accommodation) {
        if (!accommodation.getOwner().getId().equals(ownerId)) {
            throw new AccommodationException(ErrorCode.FORBIDDEN);
        }
    }
}