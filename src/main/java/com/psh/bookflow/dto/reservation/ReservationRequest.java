package com.psh.bookflow.dto.reservation;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationRequest {
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
