// static/frontend/scripts/pages/reservation.js

import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";

loadHeader();
loadFooter();

// 1️⃣ query string에서 roomId 추출
const params = new URLSearchParams(location.search);
const roomId = params.get('roomId');

if (!roomId) {
    alert('잘못된 접근입니다.');
}

// 2️⃣ DOM 요소
const form = document.getElementById('reservation-form');
const startInput = document.getElementById('startDate');
const endInput = document.getElementById('endDate');

// 3️⃣ 오늘 이전 날짜 선택 방지
const today = new Date().toISOString().split('T')[0];
startInput.min = today;
endInput.min = today;

// 4️⃣ 예약 요청 처리
form.addEventListener('submit', e => {
    e.preventDefault();

    const startDate = startInput.value;
    const endDate = endInput.value;

    if (!startDate || !endDate) {
        alert('날짜를 모두 선택해주세요.');
        return;
    }

    if (startDate >= endDate) {
        alert('종료 날짜는 시작 날짜 이후여야 합니다.');
        return;
    }

    // 🔜 나중에 fetch로 서버 전송
    alert(
        `예약 요청 완료!\n방 번호: ${roomId}\n기간: ${startDate} ~ ${endDate}`
    );
});
