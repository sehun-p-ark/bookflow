package com.psh.bookflow.dto.reservation;

import com.psh.bookflow.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {

    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long totalPrice;
    private String status;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUser().getId();
        this.roomId = reservation.getRoom().getId();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
    }
}
