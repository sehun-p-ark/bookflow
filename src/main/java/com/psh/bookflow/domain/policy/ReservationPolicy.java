package com.psh.bookflow.domain.policy;


import com.psh.bookflow.domain.Statuses.ReservationStatus;

import java.util.EnumSet;

public final class ReservationPolicy {

    private ReservationPolicy() {}

    // 방을 실제로 점유하는 예약 상태
    public static final EnumSet<ReservationStatus> ACTIVE_STATUSES =
            EnumSet.of(
                    ReservationStatus.REQUESTED,
                    ReservationStatus.CONFIRMED
            );
}