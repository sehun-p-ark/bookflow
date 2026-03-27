import { apiGet } from "/frontend/scripts/utils/api.js";
import { loadHeader } from "/frontend/scripts/components/header.js";
import { loadFooter } from "/frontend/scripts/components/footer.js";

loadHeader();
loadFooter();
// URL 상태 분기를 위한 조건
const STATE = {
    BROWSE: "BROWSE", // 전체 숙소 탐색
    SEARCH: "SEARCH"  // 예약 가능 기준 탐색
}

let state = STATE.BROWSE;
let cached = [];
let selectedFacilities = new Set();

const $list = document.getElementById("accommodation-list");
const $typeCheckboxes = document.querySelectorAll(".filter-item input");
const $resetBtn = document.querySelector(".filter-title .reload");
const $facilityButtons = document.querySelectorAll(".filter-group .facilities");

// 상태 별 화면 표시 HTML
const EMPTY_HTML = `
    <div class="empty">
        <img src="/frontend/images/accommodation/empty-accommodation.png" alt="#">
        <p>조건에 맞는 숙소가 없습니다.</p>
    </div>
`;

const LOADING_HTML = `
    <div class="loading">
        <img class="spinner" src="/frontend/images/accommodation/loading.png" alt="#">
        <p>숙소를 검색 중입니다...</p>
    </div>
`;

// URL 파라미터
function getSearchParams() {
    const params = new URLSearchParams(location.search);
    return {
        checkIn: params.get("checkIn"),
        checkOut: params.get("checkOut"),
        guest: params.get("guest")
    };
}

// hasSearchCondition(객체) <- 객체에 있는 값을 꺼내쓰는 방법
// checkIn = 객체.checkIn, 이런식으로 ㅇㅇ
function hasSearchCondition({ checkIn, checkOut, guest }) {
    // !! : boolean으로 강제 변환
    return !!(checkIn && checkOut && guest);
}

function normalizeAmenity(value) {
    return String(value || "").trim().toLowerCase();
}

function getSelectedTypes() {
    return [...$typeCheckboxes]
        .filter(c => c.checked)
        .map(c => c.value);
}

async function loadAll() {
    state = STATE.BROWSE;
    $list.innerHTML = LOADING_HTML

    try {
        const response = await apiGet("/accommodations");
        cached = response;
        renderAccommodations(response);
    } catch {
        $list.innerHTML = EMPTY_HTML;
    }
}

async function loadSearch(params) {
    state = STATE.SEARCH;
    $list.innerHTML = LOADING_HTML

    try {
        const qs = new URLSearchParams(params);
        const response = await apiGet(`/accommodations/search?${qs}`);
        cached = response;
        renderAccommodations(response);
    } catch {
        $list.innerHTML = EMPTY_HTML;
    }
}

// 숙소 리스트 렌더링
function renderAccommodations(list) {
    if (!list || list.length === 0) {
        $list.innerHTML = EMPTY_HTML;
        return;
    }

    $list.innerHTML = list.map(accommodation => {
        const amenities = (accommodation.amenities || [])
            .map(normalizeAmenity);

        const amenitiesHtml = amenities.length > 0
            ? `
                <div class="amenities">
                    ${amenities.map(amenity => `
                        <span class="amenity-chip">${amenity}</span>
                    `).join("")}
                </div>
            `
            : `
                <div class="amenities">
                    <span class="amenity-chip empty">시설 정보 없음</span>
                </div>
            `;

        return `
        <div class="accommodation-card">
            <div class="card-image">
                <img src="/frontend/images/sample-hotel.jpg" alt="${accommodation.name}">
                <span class="number">${accommodation.id}</span>
            </div>

            <div class="card-body">
                <h3 class="name">${accommodation.name}</h3>

                <div class="rating">
                    ⭐ 4.8 <span class="review">(1,234)</span>
                </div>

                <p class="address">${accommodation.address}</p>

                <div class="price-area">
                    <span class="price">
                        ${accommodation.minPrice
            ? `₩${accommodation.minPrice.toLocaleString()} / 1박`
            : "가격 정보 없음"}
                    </span>
                </div>

                ${amenitiesHtml}

                <button class="room-btn" data-id="${accommodation.id}">
                    방보기
                </button>
            </div>
        </div>
    `;
    }).join("");
}

function applyFilters() {
    const selectedTypes = getSelectedTypes();
    const selectedAmenityKeys = [...selectedFacilities];

    const filtered = cached.filter(accommodation => {
        const typeMatched =
            selectedTypes.length === 0 ||
            selectedTypes.includes(accommodation.category);

        const hotelAmenities = (accommodation.amenities || [])
            .map(normalizeAmenity);

        const amenityMatched =
            selectedAmenityKeys.length === 0 ||
            selectedAmenityKeys.every(key => hotelAmenities.includes(key));

        return typeMatched && amenityMatched;
    });

    renderAccommodations(filtered);
}

// 타입 필터
$typeCheckboxes.forEach(cb => {
    cb.addEventListener("change", applyFilters);
});

// 시설 필터
$facilityButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        const key = normalizeAmenity(btn.textContent);

        if (selectedFacilities.has(key)) {
            selectedFacilities.delete(key);
            btn.classList.remove("active");
            btn.setAttribute("aria-pressed", "false");
        } else {
            selectedFacilities.add(key);
            btn.classList.add("active");
            btn.setAttribute("aria-pressed", "true");
        }

        applyFilters();
    });
});

// 필터 초기화 (숙소유형/시설 UI 상태 리셋)
function resetFilters() {
    $typeCheckboxes.forEach(cb => {
        cb.checked = false;
    });

    $facilityButtons.forEach(btn => {
        btn.classList.remove("active");
        btn.setAttribute("aria-pressed", "false");
    });

    selectedFacilities.clear();
    renderAccommodations(cached);
}

$resetBtn?.addEventListener("click", resetFilters);

// 방보기 버튼 눌렀을 때 room으로 이동
$list.addEventListener("click", (e) => {
    // closest : 자신부터 부모요소로 올라가면서 dom요소 찾기
    const btn = e.target.closest(".room-btn");
    // btn안 말고 다른 곳 클릭하면 바로 return
    if (!btn) return;

    moveToRoom(btn.dataset.id);
});


// URL 뒤에 조건 값들이 있으면 -> 해당 방들만 조회
//                     없으면 -> 전부 조회
function moveToRoom(accommodationId) {
    const params = getSearchParams();

    if (state === STATE.SEARCH) {
        location.href =
            "/frontend/pages/room.html?" +
            new URLSearchParams({
                accommodationId,
                ...params
            });
    } else {
        location.href =
            `/frontend/pages/room.html?accommodationId=${accommodationId}`;
    }
}

// header 검색버튼 새로 눌렀을 때
window.addEventListener("search:change", (e) => {
    history.pushState({}, "", "?" + new URLSearchParams(e.detail));
    loadSearch(e.detail);
});
// 뒤로가기 버튼 눌렀을 때 원래 값 복원
window.addEventListener("popstate", init);

// 최초 실행 로직
function init() {
    const params = getSearchParams();

    if(hasSearchCondition(params)) {
        loadSearch(params);
    } else {
        loadAll();
    }
}

// 최초 실행
init();