package com.psh.bookflow;

import com.psh.bookflow.domain.*;
import com.psh.bookflow.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void saveAndFindAll() {

        // 1) User 저장
        User user = new User();
        user.setName("행님");
        user.setEmail("test@example.com");
        user.setPassword("1234");
        userRepository.save(user);

        // 2) 숙소 저장
        Accommodation acc = new Accommodation();
        acc.setName("서울 호텔");
        acc.setAddress("서울 강남구");
        acc.setCategory("HOTEL");
        acc.setOwner(user);
        acc.setStatus(AccommodationStatus.ACTIVE);  // ✔ 상태 필수값 추가

        accommodationRepository.save(acc);


        // 3) 객실 저장
        Room room = new Room();
        room.setName("디럭스룸");
        room.setPricePerNight(120000L);
        room.setCapacity(2);
        room.setAccommodation(acc);
        room.setStatus(RoomStatus.AVAILABLE);

        roomRepository.save(room);

        // 4) 예약 저장
        Reservation res = new Reservation();
        res.setUser(user);
        res.setRoom(room);
        res.setStartDate(LocalDate.of(2025, 1, 10));
        res.setEndDate(LocalDate.of(2025, 1, 12));
        res.setTotalPrice(240000L);
        res.setStatus(ReservationStatus.REQUESTED);

        reservationRepository.save(res);

        // 5) 조회 출력
        System.out.println("=== USERS ===");
        userRepository.findAll().forEach(System.out::println);

        System.out.println("=== ACCOMMODATIONS ===");
        accommodationRepository.findAll().forEach(System.out::println);

        System.out.println("=== ROOMS ===");
        roomRepository.findAll().forEach(System.out::println);

        System.out.println("=== RESERVATIONS ===");
        reservationRepository.findAll().forEach(System.out::println);
    }
}
