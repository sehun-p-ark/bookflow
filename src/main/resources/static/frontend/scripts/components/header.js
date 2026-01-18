import { apiGet, apiPost } from "/frontend/scripts/utils/api.js";

function formatDate(date) {
    return date.toISOString().split("T")[0];
}

export async function loadHeader() {
    const html = await fetch("/frontend/components/header.html")
        .then(r => r.text());
    document.getElementById("header").innerHTML = html;

    const path = location.pathname;
    const showSearch =
        path.includes("accommodation.html") ||
        path.includes("room.html");

    if (showSearch) {
        initSearchBar();
    }

    handleLoginState();
}

function initSearchBar() {
    const params = new URLSearchParams(location.search);

    const $searchBar = document.getElementById("search-bar");
    const $searchBtn = $searchBar.querySelector(".search-btn");
    const $checkIn = document.getElementById("check-in");
    const $checkOut = document.getElementById("check-out");

    const $guestDisplay = document.getElementById("guestDisplay");
    const $guestPanel = document.getElementById("guestPanel");
    const $guestCount = document.getElementById("guestCount");
    const $guestCountText = document.getElementById("guestCountText");
    const $plusBtn = document.getElementById("plusBtn");
    const $minusBtn = document.getElementById("minusBtn");

    $searchBar.classList.remove("hidden");

    const today = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);

    $checkIn.value = params.get("checkIn") || formatDate(today);
    $checkOut.value = params.get("checkOut") || formatDate(tomorrow);

    let guestCount = Number(params.get("guest")) || 2;
    updateGuest();

    // 인원 패널 토글 (버튼 클릭)
    $guestDisplay.addEventListener("click", (e) => {
        e.stopPropagation();
        $guestPanel.classList.toggle("open");
    });
    // 패널 자체 클릭 시 닫히지 않게
    $guestPanel.addEventListener("click", (e) => {
        e.stopPropagation();
    });
    // 패널 밖 클릭 시 닫기
    document.addEventListener("click", (e) => {
        const clickedInside =
            $guestPanel.contains(e.target) ||
            $guestDisplay.contains(e.target);

        if (!clickedInside) {
            $guestPanel.classList.remove("open");
        }
    });

    $plusBtn.onclick = () => {
        guestCount++;
        updateGuest();
    };

    $minusBtn.onclick = () => {
        if (guestCount > 1) {
            guestCount--;
            updateGuest();
        }
    };

    function updateGuest() {
        $guestCount.textContent = guestCount;
        $guestCountText.textContent = guestCount;
    }

    // 검색버튼 눌렀을 떄
    $searchBtn.onclick = () => {
        if (!$checkIn.value || !$checkOut.value) {
            alert("날짜를 선택해주세요.");
            return;
        }

        const checkIn = $checkIn.value;
        const checkOut = $checkOut.value;
        const guest = guestCount;

        const qs = new URLSearchParams({ checkIn, checkOut, guest });

        // URL만 변경
        history.pushState(
            { checkIn, checkOut, guest },
            "",
            `?${qs.toString()}`
        );

        // 페이지들에 알림
        window.dispatchEvent(
            new CustomEvent("search:change", {
                detail: { checkIn, checkOut, guest }
            })
        );
    };
}

async function handleLoginState() {
    const $guestLinks = document.getElementById("guest-links");
    const $userLinks = document.getElementById("user-links");
    const $logoutBtn = document.getElementById("header-logout");

    try {
        // 로그인 확인
        await apiGet("/users/me");

        // 로그인 상태
        if ($guestLinks) $guestLinks.hidden = true;
        if ($userLinks) $userLinks.hidden = false;

    } catch {
        // 비로그인 상태
        if ($guestLinks) $guestLinks.hidden = false;
        if ($userLinks) $userLinks.hidden = true;
    }

    // 로그아웃
    $logoutBtn?.addEventListener("click", async (e) => {
        e.preventDefault();
        await apiPost("/users/logout");
        location.href = "/frontend/pages/index.html";
    });
}
