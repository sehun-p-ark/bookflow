import {loadHeader} from "/frontend/scripts/components/header.js";
import {loadFooter} from "/frontend/scripts/components/footer.js";
import {apiGet, apiPost} from "/frontend/scripts/utils/api.js";

loadHeader();
loadFooter();

const loginArea = document.getElementById("login-area");
const profileArea = document.getElementById("profile-area");

const loginForm = document.getElementById("login-form");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");

const userEmail = document.getElementById("user-email");
const userName = document.getElementById("user-name");
const logoutBtn = document.getElementById("logout-btn");
const deleteBtn = document.getElementById("delete-btn");

const accountNav = document.getElementById("account-nav");
const navButtons = accountNav?.querySelectorAll(".nav-btn") || [];
const panels = document.querySelectorAll(".account-panel");

const profileForm = document.getElementById("profile-form");
const profileNameInput = document.getElementById("profile-name");

const reviewModal = document.getElementById("review-modal");
const reviewForm = document.getElementById("review-form");
const reviewReservationIdInput = document.getElementById("review-reservation-id");
const reviewRatingInput = document.getElementById("review-rating");
const reviewContentInput = document.getElementById("review-content");
const reviewCancelBtn = document.getElementById("review-cancel-btn");

const sectionMap = {
    UPCOMING: document.querySelector('.reservation-list[data-section="UPCOMING"]'),
    COMPLETED: document.querySelector('.reservation-list[data-section="COMPLETED"]'),
    CANCELED: document.querySelector('.reservation-list[data-section="CANCELED"]')
};

const STATUS_GROUP = {
    UPCOMING: ["REQUESTED", "CONFIRMED"],
    COMPLETED: ["COMPLETED"],
    CANCELED: ["CANCELED"]
};

const CANCELABLE_STATE = ["REQUESTED", "CONFIRMED"];

const SAMPLE_ACCOMMODATIONS = [
    {
        id: 1,
        name: "북플로우 호텔 강남",
        location: "서울 강남구",
        price: "₩90,000 / 1박",
        desc: "강남의 중심에 위치한 고급 호텔",
        image: "/frontend/images/sample-hotel.jpg"
    },
    {
        id: 2,
        name: "북플로우 호텔 홍대",
        location: "서울 마포구",
        price: "₩120,000 / 1박",
        desc: "트렌디한 홍대 인근의 모던한 숙소",
        image: "/frontend/images/sample-room.jpg"
    }
];

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function setAuthView(isLoggedIn) {
    loginArea.hidden = isLoggedIn;
    profileArea.hidden = !isLoggedIn;
}

function switchPanel(panelKey) {
    navButtons.forEach((btn) => {
        btn.classList.toggle("active", btn.dataset.panel === panelKey);
    });

    panels.forEach((panel) => {
        panel.classList.toggle("active", panel.dataset.panel === panelKey);
    });
}

function bindPanelNavigation() {
    accountNav?.addEventListener("click", (e) => {
        const btn = e.target.closest(".nav-btn");
        if (!btn) return;
        switchPanel(btn.dataset.panel);
    });
}

function bindPasswordToggles() {
    document.querySelectorAll(".toggle-password").forEach((btn) => {
        btn.addEventListener("click", () => {
            const target = document.getElementById(btn.dataset.target);
            if (!target) return;

            const showPassword = target.type === "password";
            target.type = showPassword ? "text" : "password";
            btn.innerHTML = showPassword
                ? "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"24px\" width=\"24px\" viewBox=\"0 -960 960 960\"><path d=\"M607.5-372.5Q660-425 660-500t-52.5-127.5Q555-680 480-680t-127.5 52.5Q300-575 300-500t52.5 127.5Q405-320 480-320t127.5-52.5Zm-204-51Q372-455 372-500t31.5-76.5Q435-608 480-608t76.5 31.5Q588-545 588-500t-31.5 76.5Q525-392 480-392t-76.5-31.5ZM214-281.5Q94-363 40-500q54-137 174-218.5T480-800q146 0 266 81.5T920-500q-54 137-174 218.5T480-200q-146 0-266-81.5ZM480-500Zm207.5 160.5Q782-399 832-500q-50-101-144.5-160.5T480-720q-113 0-207.5 59.5T128-500q50 101 144.5 160.5T480-280q113 0 207.5-59.5Z\"/></svg>"
                : "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"24px\" width=\"24px\" viewBox=\"0 -960 960 960\"><path d=\"m644-428-58-58q9-47-27-88t-93-32l-58-58q17-8 34.5-12t37.5-4q75 0 127.5 52.5T660-500q0 20-4 37.5T644-428Zm128 126-58-56q38-29 67.5-63.5T832-500q-50-101-143.5-160.5T480-720q-29 0-57 4t-55 12l-62-62q41-17 84-25.5t90-8.5q151 0 269 83.5T920-500q-23 59-60.5 109.5T772-302Zm20 246L624-222q-35 11-70.5 16.5T480-200q-151 0-269-83.5T40-500q21-53 53-98.5t73-81.5L56-792l56-56 736 736-56 56ZM222-624q-29 26-53 57t-41 67q50 101 143.5 160.5T480-280q20 0 39-2.5t39-5.5l-36-38q-11 3-21 4.5t-21 1.5q-75 0-127.5-52.5T300-500q0-11 1.5-21t4.5-21l-84-82Zm319 93Zm-151 75Z\"/></svg>";
        });
    });
}

function renderMyAccommodations() {
    const listEl = document.getElementById("my-accommodation-list");
    if (!listEl) return;

    if (SAMPLE_ACCOMMODATIONS.length === 0) {
        renderEmpty(listEl, "등록된 숙소가 없습니다.");
        return;
    }

    listEl.innerHTML = SAMPLE_ACCOMMODATIONS.map((item) => `
        <article class="reservation-card host-card">
            <img src="${escapeHtml(item.image)}" alt="${escapeHtml(item.name)}">
            <div class="card-body">
                <h4>${escapeHtml(item.name)}</h4>
                <p><svg xmlns="http://www.w3.org/2000/svg" height="18px" viewBox="0 -960 960 960" width="18px" fill="#5a677d"><path d="M536.5-503.5Q560-527 560-560t-23.5-56.5Q513-640 480-640t-56.5 23.5Q400-593 400-560t23.5 56.5Q447-480 480-480t56.5-23.5ZM480-186q122-112 181-203.5T720-552q0-109-69.5-178.5T480-800q-101 0-170.5 69.5T240-552q0 71 59 162.5T480-186Zm0 106Q319-217 239.5-334.5T160-552q0-150 96.5-239T480-880q127 0 223.5 89T800-552q0 100-79.5 217.5T480-80Zm0-480Z"/></svg>
                    ${escapeHtml(item.location)}</p>
                <p class="price">${escapeHtml(item.price)}</p>
                <p>${escapeHtml(item.desc)}</p>
            </div>
            <div class="card-actions vertical">
                <button type="button" class="light-btn">수정</button>
                <button type="button" class="danger-btn">삭제</button>
            </div>
        </article>
    `).join("");
}

function formatPrice(value) {
    return `₩${Number(value || 0).toLocaleString()}`;
}

function renderReservationCard(reservation, type) {
    const reviewButton = type === "COMPLETED"
        ? `<button class="light-btn btn-review" data-id="${reservation.id}">리뷰 작성</button>`
        : "";

    const cancelButton = CANCELABLE_STATE.includes(reservation.status)
        ? `<button class="danger-outline-btn btn-cancel" data-id="${reservation.id}">예약 취소</button>`
        : "";

    return `
        <article class="reservation-card">
            <img src="/frontend/images/sample-hotel.jpg" alt="${escapeHtml(reservation.accommodationName)}">
            <div class="card-body">
                <h4>${escapeHtml(reservation.accommodationName)}</h4>
                <p>${escapeHtml(reservation.roomName)}</p>
                <p>체크인: ${escapeHtml(reservation.checkInDate)} · 체크아웃: ${escapeHtml(reservation.checkOutDate)}</p>
                <p class="price">총 결제 금액 ${formatPrice(reservation.totalPrice)}</p>
            </div>
            <div class="card-actions">
                ${cancelButton}
                ${reviewButton}
                <a class="btn-primary detail-btn" href="/frontend/pages/room.html?accommodationId=${reservation.accommodationId}">예약 상세</a>
            </div>
        </article>
    `;
}

function renderEmpty(target, text = "예약 내역이 없습니다.") {
    if (!target) return;
    target.innerHTML = `
        <div class="empty-state">
            <img src="/frontend/images/empty-reservation.png" alt="empty">
            <p>${escapeHtml(text)}</p>
        </div>
    `;
}

function renderReservationStatus(reservations) {
    Object.entries(sectionMap).forEach(([key, listEl]) => {
        if (!listEl) return;

        const filtered = reservations.filter((reservation) =>
            STATUS_GROUP[key].includes(reservation.status)
        );

        if (filtered.length === 0) {
            renderEmpty(listEl, key === "CANCELED" ? "취소된 예약이 없습니다." : "예약 내역이 없습니다.");
            return;
        }

        listEl.innerHTML = filtered.map((item) => renderReservationCard(item, key)).join("");
    });
}

async function loadMyReservations() {
    try {
        const reservations = await apiGet("/reservations/me");
        renderReservationStatus(reservations);
    } catch {
        Object.values(sectionMap).forEach((target) => renderEmpty(target));
    }
}

async function checkLoginStatus() {
    try {
        const me = await apiGet("/users/me");

        setAuthView(true);
        userEmail.textContent = me.email;
        userName.textContent = me.name;
        profileNameInput.value = me.name || "";

        renderMyAccommodations();
        await loadMyReservations();
    } catch {
        setAuthView(false);
    }
}

function openReviewModal(reservationId) {
    if (!reviewModal) return;

    reviewReservationIdInput.value = reservationId;
    reviewRatingInput.value = "5";
    reviewContentInput.value = "";

    reviewModal.classList.remove("hidden");
    reviewModal.setAttribute("aria-hidden", "false");
}

function closeReviewModal() {
    if (!reviewModal) return;

    reviewModal.classList.add("hidden");
    reviewModal.setAttribute("aria-hidden", "true");
}

function bindReviewModalEvents() {
    reviewForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const reservationId = Number(reviewReservationIdInput.value);
        const rating = Number(reviewRatingInput.value);
        const content = reviewContentInput.value.trim();

        if (!reservationId) {
            alert("잘못된 접근입니다.");
            return;
        }
        if (!rating || rating < 1 || rating > 5) {
            alert("별점은 1~5 사이로 입력해주세요.");
            return;
        }
        if (!content) {
            alert("리뷰 내용을 입력해주세요.");
            return;
        }

        try {
            await apiPost("/reviews", { reservationId, rating, content });
            alert("리뷰가 등록되었습니다.");
            closeReviewModal();
            await loadMyReservations();
        } catch (error) {
            alert(error.message || "리뷰 등록에 실패했습니다.");
        }
    });

    reviewModal?.addEventListener("click", (e) => {
        if (e.target.dataset.role === "close-review-modal") {
            closeReviewModal();
        }
    });

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && !reviewModal?.classList.contains("hidden")) {
            closeReviewModal();
        }
    });

    reviewCancelBtn?.addEventListener("click", closeReviewModal);
}

function bindAuthEvents() {
    loginForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value;
        if (!email || !password) return;

        try {
            await apiPost("/users/login", { email, password });
            location.reload();
        } catch {
            alert("로그인에 실패했습니다.");
        }
    });

    logoutBtn?.addEventListener("click", async () => {
        try {
            await apiPost("/users/logout");
            location.reload();
        } catch {
            alert("로그아웃에 실패했습니다.");
        }
    });

    deleteBtn?.addEventListener("click", () => {
        alert("회원탈퇴 기능은 백엔드 연동 후 활성화됩니다.");
    });

    profileForm?.addEventListener("submit", (e) => {
        e.preventDefault();
        alert("프로필 수정 기능은 백엔드 연동 후 활성화됩니다.");
    });
}

function bindReservationCardEvents() {
    document.querySelector(".account-main")?.addEventListener("click", async (e) => {
        const cancelBtn = e.target.closest(".btn-cancel");
        if (cancelBtn) {
            const reservationId = cancelBtn.dataset.id;
            if (!confirm("예약을 취소하시겠습니까?")) return;

            try {
                await apiPost(`/reservations/${reservationId}/cancel`);
                alert("예약이 취소되었습니다.");
                await loadMyReservations();
            } catch {
                alert("예약 취소에 실패했습니다.");
            }
            return;
        }

        const reviewBtn = e.target.closest(".btn-review");
        if (reviewBtn) {
            openReviewModal(reviewBtn.dataset.id);
        }
    });
}

bindPanelNavigation();
bindPasswordToggles();
bindReviewModalEvents();
bindAuthEvents();
bindReservationCardEvents();
checkLoginStatus();