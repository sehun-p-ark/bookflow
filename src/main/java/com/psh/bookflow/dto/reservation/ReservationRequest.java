package com.psh.bookflow.dto.reservation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationRequest {
    @NotNull
    private Long roomId;

    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @FutureOrPresent
    private LocalDate checkOut;
}
