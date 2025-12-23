package com.psh.bookflow.dto.reservation;

import com.psh.bookflow.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {

    private final Long id;
    private final Long userId;
    private final Long roomId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUser().getId();
        this.roomId = reservation.getRoom().getId();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
    }
}
