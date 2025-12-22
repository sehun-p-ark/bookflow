import { apiGet, apiPost } from "./api.js";

export function createReservation({roomId, startDate, endDate}) {
    return apiPost("/reservations", {
        roomId,
        startDate,
        endDate
    });
}

export function getMyReservations() {
    return apiGet("/reservations/me");
}
