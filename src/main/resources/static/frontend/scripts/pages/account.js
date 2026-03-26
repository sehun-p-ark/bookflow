import { loadHeader } from "/frontend/scripts/components/header.js";
import { loadFooter } from "/frontend/scripts/components/footer.js";
import { apiGet, apiPost } from "/frontend/scripts/utils/api.js";

loadHeader();
loadFooter();

/* =========================
   DOM
========================= */
const loginArea = document.getElementById("login-area");
const profileArea = document.getElementById("profile-area");

const loginForm = document.getElementById("login-form");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");

const userEmail = document.getElementById("user-email");
const userName = document.getElementById("user-name");
const logoutBtn = document.getElementById("logout-btn");

// reservation 영역
const loginNotice = document.getElementById("login-notice");
const reservationContent = document.getElementById("reservation-content");
const tabs = reservationContent?.querySelectorAll(
    ":scope > .reservation-tabs > .tab-label > .tab"
);
const sections = reservationContent?.querySelectorAll(
    ":scope > .reservation-section"
);

/* =========================
   예약 상태 상수
========================= */
const STATUS_GROUP = {
    UPCOMING: {
        statuses: ["REQUESTED", "CONFIRMED"],
        label: { REQUESTED: "예약 요청", CONFIRMED: "예약 확정" }
    },
    COMPLETED: {
        statuses: ["COMPLETED"],
        label: { COMPLETED: "이용 완료" }
    },
    CANCELED: {
        statuses: ["CANCELED"],
        label: { CANCELED: "예약 취소" }
    }
};

const CANCELABLE_STATE = ["REQUESTED", "CONFIRMED"];

/* =========================
   로그인 상태 확인
========================= */
async function checkLoginStatus() {
    try {
        const me = await apiGet("/users/me");

        // 로그인 상태
        loginArea.hidden = true;
        profileArea.hidden = false;

        userEmail.textContent = me.email;
        userName.textContent = me.name;

        // 로그인 상태면 예약도 로딩
        loadMyReservations();

    } catch {
        // 비로그인 상태
        loginArea.hidden = false;
        profileArea.hidden = true;

        loginNotice?.classList.remove("hidden");
        reservationContent?.classList.add("hidden");
    }
}

// 로그인
loginForm?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput.value.trim();
    const password = passwordInput.value;
    if (!email || !password) return;

    try {
        await apiPost("/users/login", { email, password });
        location.href = "/frontend/pages/index.html";
    } catch {
        alert("로그인에 실패했습니다.");
    }
});

// 로그아웃
logoutBtn?.addEventListener("click", async () => {
    try {
        await apiPost("/users/logout");
        location.reload();
    } catch {
        alert("로그아웃에 실패했습니다.");
    }
});

// 예약 목록 렌더링
function renderReservationStatus(reservations) {
    sections.forEach(section => {
        const key = section.dataset.section;
        const list = section.querySelector(".reservation-list");
        const status = STATUS_GROUP[key];

        const filtered = reservations.filter(r =>
            status.statuses.includes(r.status)
        );

        if (filtered.length === 0) {
            list.innerHTML = `
                <div class="empty-state">
                    <img src="/frontend/images/empty-reservation.png" class="empty-icon">
                    <p class="empty-text">아직 예약 내역이 없습니다.</p>
                </div>
            `;
            return;
        }

        list.innerHTML = filtered.map(r => {
            const canCancel = CANCELABLE_STATE.includes(r.status);
            const canWriteReview = status.statuses.includes("COMPLETED");

            return `
                <div class="card">
                    <h4>
                        <a href="/frontend/pages/room.html?accommodationId=${r.accommodationId}">
                            ${r.accommodationName}
                        </a>
                    </h4>
                    <p class="room-name">${r.roomName}</p>
                    <p class="date">기간: ${r.checkInDate} ~ ${r.checkOutDate}</p>
                    <p class="price">금액: ${r.totalPrice.toLocaleString()}원</p>
                    <p class="status status-${r.status}">
                        ${status.label[r.status]}
                    </p>

                    ${canCancel ? `
                        <button class="btn-cancel" data-id="${r.id}">
                            예약 취소
                        </button>
                    ` : ""}

                    ${canWriteReview ? `
                        <button class="btn-review" data-id="${r.id}">
                            리뷰 작성
                        </button>
                    ` : ""}
                </div>
            `;
        }).join("");
    });
}

// 내 예약 조회
async function loadMyReservations() {
    try {
        const reservations = await apiGet("/reservations/me");

        loginNotice?.classList.add("hidden");
        reservationContent?.classList.remove("hidden");

        renderReservationStatus(reservations);
    } catch {
        loginNotice?.classList.remove("hidden");
        reservationContent?.classList.add("hidden");
    }
}

// 에약 탭 전환
tabs?.forEach(tab => {
    tab.addEventListener("change", () => {
        const status = tab.dataset.status;
        sections.forEach(section => {
            section.classList.toggle(
                "hidden",
                section.dataset.section !== status
            );
        });
    });
});

// 예약 취소
reservationContent?.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("btn-cancel")) return;

    const reservationId = e.target.dataset.id;
    if (!confirm("예약을 취소하시겠습니까?")) return;

    try {
        await apiPost(`/reservations/${reservationId}/cancel`);
        alert("예약이 취소되었습니다.");
        loadMyReservations();
    } catch {
        alert("예약 취소에 실패했습니다.");
    }
});

    // TODO: "리뷰 작성" 버튼 클릭 동작(페이지 이동/모달/작성 API 연동) 정의 후 이벤트 핸들러 추가
/* =========================
   최초 실행
========================= */
checkLoginStatus();
