import { apiGet, apiPost } from "./api.js";

export function createReservation(data) {
    return apiPost("/reservations", data);
}

export function getMyReservations() {
    return apiGet("/reservations/me");
}
