import {apiGet} from "../api";

const list = document.getElementById('accommodation-list');

if (!list) {
    console.error('accommodation-list 요소를 찾을 수 없습니다.');
}

async function loadAccommodations() {
    try {
        const accommodations = await apiGet("/accommodations");

        list.innerHTML = accommodations.map(accommodation => `
        <div class="card">
            <h3>${accommodation.name}</h3>
            <p>${accommodation.address}</p>
            <p>${accommodation.price.toLocaleString()}원</p>
            <a href="/frontend/pages/room.html?accommodationId=${accommodation.id}">
                방보기
            </a>
        </div>
        `).join('');

    } catch (e) {
        console.error("숙소 목록 조회 실패", e);
        list.innerHTML = "<p>숙소 정보를 불러오지 못했습니다.</p>";
    }
}

loadAccommodations();