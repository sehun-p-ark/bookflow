package com.psh.bookflow.service;

import com.psh.bookflow.domain.Room;
import com.psh.bookflow.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("객실을 찾을 수 없습니다."));
    }

    @Transactional
    public Room save(Room room) {
        return roomRepository.save(room);
    }
}
