import { loadHeader } from "/frontend/scripts/components/header.js";
import { loadFooter } from "/frontend/scripts/components/footer.js";
import { apiGet, apiPost } from "/frontend/scripts/utils/api.js";

loadHeader();
loadFooter();

// DOM
const checkInInput = document.getElementById("checkIn");
const checkOutInput = document.getElementById("checkOut");
const reserveBtn = document.getElementById("reserve-btn");

const roomNameEl = document.querySelector(".room-name");
const accommodationNameEl = document.querySelector(".accommodation-name");
const roomPriceEl = document.querySelector(".room-price");
const totalPriceEl = document.querySelector(".price-summary strong");
const guestCountEl = document.querySelector(".guest-count");

// Query Params
const params = new URLSearchParams(location.search);
const roomId = params.get("roomId");
const checkInParam = params.get("checkIn");
const checkOutParam = params.get("checkOut");
const guestParam = params.get("guest");

if (!roomId) {
    alert("잘못된 접근입니다.");
    location.href = "/frontend/pages/accommodation.html";
}

// 1박 당 가격
let pricePerNight = 0;

// 방 정보 서버 조회
async function loadRoom() {
    try {
        const room = await apiGet(`/rooms/${roomId}`);

        roomNameEl.textContent = room.name;
        accommodationNameEl.textContent = room.accommodationName;
        roomPriceEl.textContent = `₩${room.price.toLocaleString()} / 1박`;

        pricePerNight = room.price;
        updateTotalPrice();

    } catch (e) {
        alert("방 정보를 불러오지 못했습니다.");
        console.error(e);
    }
}

// URL에 있는 값을 화면 표시
function applyReservationParams() {
    // 날짜 표시
    if (checkInParam) {
        checkInInput.value = checkInParam;
        checkOutInput.min = checkInParam;
    }

    if (checkOutParam) {
        checkOutInput.value = checkOutParam;
    }

    // 인원 표시
    if (guestParam) {
        guestCountEl.textContent = `${guestParam}명`;
    }

    updateTotalPrice();
}

// 날짜 계산
function calculateNights() {
    const checkIn = new Date(checkInInput.value);
    const checkOut = new Date(checkOutInput.value);

    if (!checkInInput.value || !checkOutInput.value) return 0;

    const diff = checkOut - checkIn; // (type)Date - date = ms로 값이 나옴
    const nights = diff / (1000 * 60 * 60 * 24); // 하루 24시간 기준 ms로 나누기

    return nights > 0 ? nights : 0;
}

// 총 금액 계산
function updateTotalPrice() {
    const nights = calculateNights();

    if (nights === 0) {
        totalPriceEl.textContent = "₩0";
        reserveBtn.disabled = true;
        return;
    }

    const total = nights * pricePerNight;
    totalPriceEl.textContent = `₩${total.toLocaleString()}`;
    reserveBtn.disabled = false;
}

// 날짜 변경 이벤트
checkInInput.addEventListener("change", () => {
    checkOutInput.min = checkInInput.value;
    updateTotalPrice();
});

checkOutInput.addEventListener("change", updateTotalPrice);

// 예약 요청
reserveBtn.addEventListener("click", async () => {
    const checkIn = checkInInput.value;
    const checkOut = checkOutInput.value;

    if (!checkIn || !checkOut) {
        alert("숙박 날짜를 선택해주세요.");
        return;
    }

    try {
        await apiPost("/reservations", {
            roomId,
            checkIn,
            checkOut
        });

        alert("예약 요청이 완료되었습니다.");
        location.href = "/frontend/pages/account.html";

    } catch (e) {
        if (e.status === 401) {
            alert("로그인이 필요합니다.");
            location.href = "/frontend/pages/account.html";
            return;
        }

        alert(e.message || "예약에 실패했습니다.");
        console.error(e);
    }
});

/* =========================
   Init
========================= */
loadRoom();
applyReservationParams();
