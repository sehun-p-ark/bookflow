import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";
import { apiGet } from "../utils/api.js";

loadHeader();
loadFooter();

const box = document.getElementById("user-info");

async function loadMyInfo() {
    try {
        const user = await apiGet("/users/me");

        box.innerHTML = `
            <p><strong>이메일</strong>: ${user.email}</p>
            <p><strong>이름</strong>: ${user.name}</p>
        `;
    } catch (e) {
        // 401은 api.js에서 이미 처리됨
        box.innerHTML = "<p>사용자 정보를 불러올 수 없습니다.</p>";
    }
}

loadMyInfo();
