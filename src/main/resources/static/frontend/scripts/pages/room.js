import { apiGet } from "../api.js";
import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";

loadHeader();
loadFooter();

const params = new URLSearchParams(location.search);
const accommodationId = params.get('accommodationId');
const list = document.getElementById('room-list');

if (!accommodationId) {
    alert('잘못된 접근입니다.');
}
if (!list) {
    console.error('room-list 요소를 찾을 수 없습니다.');
}

async function loadRooms() {
    try {
        const rooms = await apiGet(`/accommodations/${accommodationId}/rooms`);

        list.innerHTML = rooms.map(room => `
        <div class="card">
            <h4>${room.name}</h4>
            <p>${room.price.toLocaleString()}원</p>
            <a href="/frontend/pages/reservation.html?roomId=${room.id}">
                예약하기
            </a>
        </div>
        `).join('');
    } catch (e) {
        console.error("방 목록 조회 실패", e);
        list.innerHTML = "<p>방 정보를 불러오지 못했습니다.</p>";
    }
}

loadRooms();