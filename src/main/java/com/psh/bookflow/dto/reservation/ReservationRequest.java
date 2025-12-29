package com.psh.bookflow.dto.reservation;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationRequest {
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
