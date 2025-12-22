package com.psh.bookflow.dto.room;

import lombok.Getter;

@Getter
public class RoomRequest {
    private String name;
    private int price;
    private int capacity;
    private Long accommodationId;
}
