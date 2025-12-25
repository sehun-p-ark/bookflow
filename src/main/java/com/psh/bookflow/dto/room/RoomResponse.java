package com.psh.bookflow.dto.room;

import com.psh.bookflow.domain.Room;
import com.psh.bookflow.domain.RoomStatus;
import lombok.Getter;

@Getter
public class RoomResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Integer price;
    private final Integer capacity;
    private final RoomStatus status;
    private final Long accommodationId;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.description = room.getDescription();
        this.price = room.getPrice();
        this.capacity = room.getCapacity();
        this.status = room.getStatus();
        this.accommodationId = room.getAccommodation().getId();
    }
}
