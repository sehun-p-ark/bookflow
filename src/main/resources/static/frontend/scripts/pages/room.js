import {apiGet} from "../utils/api.js";
import {loadHeader} from "../components/header.js";
import {loadFooter} from "../components/footer.js";

loadHeader();
loadFooter();

// DOM
const list = document.getElementById('room-list');
const accommodationName = document.getElementById('accommodation-name');

// URL && STATE
const params = new URLSearchParams(location.search);
const accommodationId = params.get('accommodationId');

let state = {
    checkIn: params.get('checkIn'),
    checkOut: params.get('checkOut'),
    guest: params.get('guest')
};

if (!accommodationId) {
    alert("잘못된 접근입니다.");
    location.href = "/frontend/pages/accommodation.html";
}

function hasSearchCondition({ checkIn, checkOut, guest }) {
    return !!(checkIn && checkOut && guest);
}


// 숙소 정보 조회
async function loadAccommodation() {
    try {
        const accommodation = await apiGet(
            `/accommodations/${accommodationId}`
        );

        accommodationName.textContent = accommodation.name;

    } catch (e) {
        console.error("숙소 정보 조회 실패", e);
        accommodationName.textContent = "숙소 정보 없음";
    }
}

// 숙소 내 객실 전체 조회
async function loadAll() {
    try {
        const response = await apiGet(`/accommodations/${accommodationId}/rooms`);
        renderRooms(response);
    } catch (e) {
        console.error("방 목록 조회 실패", e);
        list.innerHTML = "<p>방 정보를 불러오지 못했습니다.</p>";
    }
}

// 숙소 내 객실 조건 검색 조회
async function loadSearch({ checkIn, checkOut, guest}) {
    try {
        const response = await apiGet(
            `/accommodations/${accommodationId}/rooms/search?` +
            new URLSearchParams({ checkIn, checkOut, guest })
        );
        renderRooms(response);
    } catch {
        list.innerHTML = "<p>예약 가능한 방이 없습니다.</p>";
    }
}

function renderRooms(response) {
    if (!response || response.length === 0) {
        list.innerHTML = "<p>방 정보를 불러오지 못했습니다.</p>";
        return;
    }

    list.innerHTML = response.map(room => `
        <div class="room-card">
            <div class="room-image">
                <img src="/frontend/images/sample-room.jpg" alt="${room.name}">
            </div>

            <div class="room-info">
                <h4 class="room-name">${room.name}</h4>
                <p class="room-desc">${room.description}</p>
                <p class="room-capacity">
                    기준 ${room.capacity}인 / 최대 ${room.capacity}인
                </p>
            </div>

            <div class="room-price">
                <strong>${room.price.toLocaleString()}원 / 1박</strong>
                <button class="reserve-btn" data-room-id="${room.id}">예약하기</button>
            </div>
        </div>
        `).join('');
}

// URL에 Param 조건 유무에 따른 이동
list.addEventListener("click", (e) => {
    const btn = e.target.closest(".reserve-btn");
    if (!btn) return;

    const roomId = btn.dataset.roomId;

    if (hasSearchCondition(state)) {
        location.href =
            "/frontend/pages/reservation.html?" +
            new URLSearchParams({
                roomId,
                ...state
            });
    } else {
        location.href =
            `/frontend/pages/reservation.html?roomId=${roomId}`;
    }
});

// header에 기간이 바꼈을 떄 URL 변경
window.addEventListener("search:change", (e) => {
    state = e.detail;

    history.pushState({}, "", "?" + new URLSearchParams({
        accommodationId,
        ...state
    }));

    loadSearch(state);
});
// 뒤로가기 && 앞으로 가기 시에 URL 복구
window.addEventListener("popstate", () => {
    const params = new URLSearchParams(location.search);
    state = {
        checkIn: params.get("checkIn"),
        checkOut: params.get("checkOut"),
        guest: params.get("guest")
    };

    if (hasSearchCondition(state)) {
        loadSearch(state);
    } else {
        loadAll();
    }
});

// 최초 실행
loadAccommodation();
if (hasSearchCondition(state)) {
    loadSearch(state);
} else {
    loadAll();
}

