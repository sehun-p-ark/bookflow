import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";
import { createReservation } from "../reservationApi.js";

loadHeader();
loadFooter();

// query string에서 roomId 추출
const params = new URLSearchParams(location.search);
const roomId = params.get('roomId');

if (!roomId) {
    return;
}

// DOM 요소
const form = document.getElementById('reservation-form');
const startInput = document.getElementById('startDate');
const endInput = document.getElementById('endDate');

// 오늘 이전 날짜 선택 방지
const today = new Date().toISOString().split('T')[0];
startInput.min = today;
endInput.min = today;

// 예약 요청 처리
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const startDate = startInput.value;
    const endDate = endInput.value;

    if (!startDate || !endDate || startDate >= endDate) {
        return;
    }

    // api 호출
    await createReservation({
        roomId: Number(roomId),
        startDate,
        endDate
    })
});
