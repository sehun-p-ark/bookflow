import { apiPost } from "/frontend/scripts/utils/api.js";
import { loadHeader } from "/frontend/scripts/components/header.js";

loadHeader();

const signForm = document.getElementById("signup-form");

signForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("password-check").value;
    const name = document.getElementById("name").value.trim();
    const birth = document.getElementById('birth').value;
    const phone = document.getElementById('phone').value;

    if (password !== passwordConfirm) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    try {
        await apiPost("/users/", {
            email,
            password,
            name,
            birth,
            phone
        });

        alert("회원가입이 완료되었습니다.");
        location.href = "/frontend/pages/account.html";

    } catch (e) {
        alert(e.message || "회원가입 실패");
    }
});
