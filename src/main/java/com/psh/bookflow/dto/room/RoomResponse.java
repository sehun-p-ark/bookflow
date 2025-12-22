package com.psh.bookflow.dto.room;

import com.psh.bookflow.domain.Room;
import lombok.Getter;

@Getter
public class RoomResponse {

    private Long id;
    private String name;
    private int price;
    private int capacity;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.price = room.getPrice();
        this.capacity = room.getCapacity();
    }
}
