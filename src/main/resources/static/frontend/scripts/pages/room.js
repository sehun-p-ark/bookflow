import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";

loadHeader();
loadFooter();

const params = new URLSearchParams(location.search);
const accommodationId = params.get('accommodationId');

if (!accommodationId) {
    alert('잘못된 접근입니다.');
}

const rooms = [
    { id: 1, name: '스탠다드 룸', price: 70000 },
    { id: 2, name: '패밀리 룸', price: 120000 }
];

const list = document.getElementById('room-list');

if (!list) {
    console.error('room-list 요소를 찾을 수 없습니다.');
}

list.innerHTML = rooms.map(room => `
    <div class="card">
        <h4>${room.name}</h4>
        <p>${room.price.toLocaleString()}원</p>
        <a href="/frontend/pages/reservation.html?roomId=${room.id}">
            예약하기
        </a>
    </div>
`).join('');
