package com.psh.bookflow.dto.room;

import com.psh.bookflow.domain.Room;
import lombok.Getter;

@Getter
public class RoomResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final int capacity;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.price = room.getPrice();
        this.capacity = room.getCapacity();
    }
}
