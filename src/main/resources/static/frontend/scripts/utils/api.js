const BASE_URL = "/api";

/* fetch 요청 */
async function request(path, options = {}) {
    const res = await fetch(`${BASE_URL}${path}`, {
        // 세션 기반 로그인 유지를 위해 include(요청 시 쿠키를 같이 보냄)
        // 서버가 Set-Cookie로 쿠키 내려주면 브라우가 저장도 함
        credentials: "include", ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    // 204 No Content
    if (res.status === 204) {
        return null;
    }

    // 실패 처리
    if (!res.ok) {
        let error;
        try {
            error = await res.json();
        } catch {
            error = { message: "서버 오류" };
        }
        throw error;
    }

    return res.json();
}

export function apiGet(path) {
    return request(path);
}

export function apiPost(path, body) {
    return request(path, {
        method: "POST",
        body: JSON.stringify(body)
    });
}
