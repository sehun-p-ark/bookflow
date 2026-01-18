package com.psh.bookflow.dto.reservation;

import com.psh.bookflow.domain.Reservation;
import com.psh.bookflow.domain.Statuses.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {

    private final Long id;
    private final Long userId;
    private final Long accommodationId;
    private final String accommodationName;
    private final String roomName;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final ReservationStatus status;
    private final Long totalPrice;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUser().getId();
        this.accommodationId = reservation.getRoom().getAccommodation().getId();
        this.accommodationName = reservation.getRoom().getAccommodation().getName();
        this.roomName = reservation.getRoom().getName();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
        this.status = reservation.getStatus();
        this.totalPrice = reservation.getTotalPrice();
    }
}
