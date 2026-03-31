import { loadHeader } from "/frontend/scripts/components/header.js";
import { loadFooter } from "/frontend/scripts/components/footer.js";
import { apiDelete, apiGet, apiPatch, apiPost, apiUpload } from "/frontend/scripts/utils/api.js";

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
const currentPasswordInput = document.getElementById("current-password");
const newPasswordInput = document.getElementById("new-password");
const newPasswordConfirmInput = document.getElementById("new-password-confirm");

const addAccommodationBtn = document.querySelector(".add-btn");
const accommodationModal = document.getElementById("accommodation-modal");
const accommodationForm = document.getElementById("accommodation-form");
const accommodationModalTitle = document.getElementById("accommodation-modal-title");
const accommodationCancelBtn = document.getElementById("accommodation-cancel-btn");
const roomFormHelp = document.getElementById("room-form-help");

const accommodationNameInput = document.getElementById("accommodation-name");
const accommodationAddressInput = document.getElementById("accommodation-address");
const accommodationCategoryInput = document.getElementById("accommodation-category");
const accommodationPhoneInput = document.getElementById("accommodation-phone");
const accommodationDescriptionInput = document.getElementById("accommodation-description");
const accommodationImageFileInput = document.getElementById("accommodation-image-file");

const roomNameInput = document.getElementById("room-name");
const roomDescriptionInput = document.getElementById("room-description");
const roomCapacityInput = document.getElementById("room-capacity");
const roomPriceInput = document.getElementById("room-price");
const roomImageFileInput = document.getElementById("room-image-file");

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

let myAccommodations = [];
let editingAccommodationId = null;

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
            btn.textContent = showPassword ? "숨김" : "보기";
        });
    });
}

function renderMyAccommodations() {
    const listEl = document.getElementById("my-accommodation-list");
    if (!listEl) return;

    if (myAccommodations.length === 0) {
        renderEmpty(listEl, "등록된 숙소가 없습니다.");
        return;
    }

    listEl.innerHTML = myAccommodations.map((item) => `
        <article class="reservation-card host-card">
            <img src="${escapeHtml(item.imageUrls?.[0] || "/frontend/images/sample-hotel.jpg")}" alt="${escapeHtml(item.name)}">
            <div class="card-body">
                <h4>${escapeHtml(item.name)}</h4>
                <p>지역: ${escapeHtml(item.address || "미등록")}</p>
                <p>연락처: ${escapeHtml(item.phone || "미등록")}</p>
                <p class="price">카테고리: ${escapeHtml(item.category || "미등록")}</p>
                <p>${escapeHtml(item.description || "한 줄 설명 없음")}</p>
            </div>
            <div class="card-actions vertical">
                <button type="button" class="light-btn btn-accommodation-edit" data-id="${item.id}">수정</button>
                <button type="button" class="danger-btn btn-accommodation-delete" data-id="${item.id}">삭제</button>
            </div>
        </article>
    `).join("");
}

async function loadMyAccommodations() {
    try {
        myAccommodations = await apiGet("/host/accommodations");
        renderMyAccommodations();
    } catch {
        myAccommodations = [];
        renderMyAccommodations();
    }
}

function setRoomInputsEnabled(enabled) {
    [roomNameInput, roomDescriptionInput, roomCapacityInput, roomPriceInput, roomImageFileInput].forEach((input) => {
        if (!input) return;
        input.disabled = !enabled;
        input.required = enabled;
    });
}

function openAccommodationModal(defaultValue = null) {
    if (!accommodationModal) return;

    editingAccommodationId = defaultValue?.id ?? null;
    const isEditMode = Boolean(editingAccommodationId);

    accommodationModalTitle.textContent = isEditMode ? "숙소 수정" : "숙소/방 등록";
    roomFormHelp.textContent = isEditMode
        ? "수정 모드에서는 숙소 정보만 변경됩니다. 객실은 별도 객실 관리에서 추가/수정해 주세요."
        : "숙소 등록 시 대표 객실 1개를 함께 등록합니다.";

    accommodationNameInput.value = defaultValue?.name ?? "";
    accommodationAddressInput.value = defaultValue?.address ?? "";
    accommodationCategoryInput.value = defaultValue?.category?.toLowerCase?.() ?? "";
    accommodationPhoneInput.value = defaultValue?.phone ?? "";
    accommodationDescriptionInput.value = defaultValue?.description ?? "";
    accommodationImageFileInput.value = "";

    roomNameInput.value = "";
    roomDescriptionInput.value = "";
    roomCapacityInput.value = "";
    roomPriceInput.value = "";
    roomImageFileInput.value = "";

    setRoomInputsEnabled(!isEditMode);

    accommodationModal.classList.remove("hidden");
    accommodationModal.setAttribute("aria-hidden", "false");
}

function closeAccommodationModal() {
    if (!accommodationModal) return;

    editingAccommodationId = null;
    accommodationForm?.reset();
    setRoomInputsEnabled(true);
    roomFormHelp.textContent = "숙소 등록 시 대표 객실 1개를 함께 등록합니다.";
    accommodationModal.classList.add("hidden");
    accommodationModal.setAttribute("aria-hidden", "true");
}

function getAccommodationPayload() {
    return {
        name: accommodationNameInput.value.trim(),
        address: accommodationAddressInput.value.trim(),
        category: accommodationCategoryInput.value.trim().toLowerCase(),
        phone: accommodationPhoneInput.value.trim(),
        description: accommodationDescriptionInput.value.trim(),
        imageUrls: []
    };
}

function getRoomPayload() {
    return {
        name: roomNameInput.value.trim(),
        description: roomDescriptionInput.value.trim(),
        capacity: Number(roomCapacityInput.value),
        price: Number(roomPriceInput.value),
        imageUrl: ""
    };
}

function validateAccommodationPayload(payload) {
    if (!payload.name || !payload.address || !payload.phone || !payload.category || !payload.description) {
        alert("숙소 정보(숙소명/지역/전화번호/카테고리/한 줄 설명)를 모두 입력해주세요.");
        return false;
    }
    return true;
}

function validateRoomPayload(payload) {
    if (!payload.name || !payload.description || !payload.capacity || !payload.price) {
        alert("방 정보(방 이름/설명/수용인원/1박 비용)를 모두 입력해주세요.");
        return false;
    }
    if (payload.capacity < 1) {
        alert("수용인원은 1명 이상이어야 합니다.");
        return false;
    }
    if (payload.price < 1000) {
        alert("1박 비용은 1,000원 이상으로 입력해주세요.");
        return false;
    }
    return true;
}

async function uploadImageFile(file) {
    const formData = new FormData();
    formData.append("file", file);
    const uploaded = await apiUpload("/files/images", formData);
    return uploaded.url;
}

function normalizeCategory(category) {
    const allowed = ["hotel", "motel", "pension", "resort", "etc"];
    if (!allowed.includes(category)) {
        throw new Error("카테고리는 hotel/motel/pension/resort/etc 중 하나여야 합니다.");
    }
    return category;
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

        await loadMyAccommodations();
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

    profileForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const changedName = profileNameInput.value.trim();
        const currentPassword = currentPasswordInput.value.trim();
        const newPassword = newPasswordInput.value.trim();
        const newPasswordConfirm = newPasswordConfirmInput.value.trim();

        try {
            if (changedName && changedName !== userName.textContent) {
                const updated = await apiPatch("/users/me/profile", { name: changedName });
                userName.textContent = updated.name;
                profileNameInput.value = updated.name;
            }

            const wantsPasswordUpdate = currentPassword || newPassword || newPasswordConfirm;
            if (wantsPasswordUpdate) {
                if (!currentPassword || !newPassword || !newPasswordConfirm) {
                    alert("비밀번호 변경 항목을 모두 입력해주세요.");
                    return;
                }
                if (newPassword !== newPasswordConfirm) {
                    alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
                    return;
                }

                await apiPatch("/users/me/password", {
                    currentPassword,
                    newPassword
                });
            }

            currentPasswordInput.value = "";
            newPasswordInput.value = "";
            newPasswordConfirmInput.value = "";
            alert("계정 정보가 저장되었습니다.");
        } catch (error) {
            alert(error.message || "계정 정보 저장에 실패했습니다.");
        }
    });
}

function bindReservationCardEvents() {
    document.querySelector(".account-main")?.addEventListener("click", async (e) => {
        const accommodationEditBtn = e.target.closest(".btn-accommodation-edit");
        if (accommodationEditBtn) {
            const accommodationId = Number(accommodationEditBtn.dataset.id);
            const target = myAccommodations.find((item) => item.id === accommodationId);
            if (!target) return;
            openAccommodationModal(target);
            return;
        }

        const accommodationDeleteBtn = e.target.closest(".btn-accommodation-delete");
        if (accommodationDeleteBtn) {
            const accommodationId = Number(accommodationDeleteBtn.dataset.id);
            if (!confirm("정말 숙소를 삭제하시겠습니까?")) return;

            try {
                await apiDelete(`/host/accommodations/${accommodationId}`);
                await loadMyAccommodations();
                alert("숙소가 삭제되었습니다.");
            } catch (error) {
                alert(error.message || "숙소 삭제에 실패했습니다.");
            }
            return;
        }

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

function bindAccommodationEvents() {
    addAccommodationBtn?.addEventListener("click", () => {
        openAccommodationModal();
    });

    accommodationForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const accommodationPayload = getAccommodationPayload();
        if (!validateAccommodationPayload(accommodationPayload)) return;

        const isEditMode = Boolean(editingAccommodationId);

        try {
            accommodationPayload.category = normalizeCategory(accommodationPayload.category);

            if (isEditMode) {
                const imageFile = accommodationImageFileInput.files?.[0];
                const currentImage = myAccommodations.find((item) => item.id === editingAccommodationId)?.imageUrls?.[0];
                if (imageFile) {
                    accommodationPayload.imageUrls = [await uploadImageFile(imageFile)];
                } else if (currentImage) {
                    accommodationPayload.imageUrls = [currentImage];
                } else {
                    alert("숙소 이미지는 최소 1장 필요합니다.");
                    return;
                }

                await apiPatch(`/host/accommodations/${editingAccommodationId}`, accommodationPayload);
                alert("숙소 정보가 수정되었습니다.");
            } else {
                const roomPayload = getRoomPayload();
                if (!validateRoomPayload(roomPayload)) return;

                const accommodationImageFile = accommodationImageFileInput.files?.[0];
                const roomImageFile = roomImageFileInput.files?.[0];
                if (!accommodationImageFile || !roomImageFile) {
                    alert("숙소 이미지와 방 이미지는 각각 최소 1장 필요합니다.");
                    return;
                }

                accommodationPayload.imageUrls = [await uploadImageFile(accommodationImageFile)];
                roomPayload.imageUrl = await uploadImageFile(roomImageFile);

                const createdAccommodation = await apiPost("/host/accommodations", accommodationPayload);
                await apiPost("/rooms", {
                    ...roomPayload,
                    accommodationId: createdAccommodation.id
                });
                alert("숙소와 대표 객실이 등록되었습니다.");
            }

            await loadMyAccommodations();
            closeAccommodationModal();
        } catch (error) {
            alert(error.message || "숙소 저장에 실패했습니다.");
        }
    });

    accommodationModal?.addEventListener("click", (e) => {
        if (e.target.dataset.role === "close-accommodation-modal") {
            closeAccommodationModal();
        }
    });

    accommodationCancelBtn?.addEventListener("click", closeAccommodationModal);
}

bindPanelNavigation();
bindPasswordToggles();
bindReviewModalEvents();
bindAuthEvents();
bindReservationCardEvents();
bindAccommodationEvents();
checkLoginStatus();