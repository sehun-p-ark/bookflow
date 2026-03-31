const BASE_URL = "/api";

/* fetch 요청 */
async function request(path, options = {}) {
    const isFormData = options.body instanceof FormData;

    const headers = {
        ...(isFormData ? {} : { "Content-Type": "application/json" }),
        ...(options.headers || {})
    };

    let res;
    try {
        res = await fetch(`${BASE_URL}${path}`, {
            credentials: "include",
            ...options,
            headers
        });
    } catch {
        throw {
            status: 0,
            code: "NETWORK_ERROR",
            message: "네트워크 오류가 발생했습니다."
        };
    }

    // 204 No Content
    if (res.status === 204) {
        return null;
    }

    // 실패 처리
    if (!res.ok) {
        const fallback = {
            status: res.status,
            code: "HTTP_ERROR",
            message: "요청 처리 중 오류가 발생했습니다.",
            path
        };

        let data;
        try {
            data = await res.json();
        } catch {
            throw fallback;
        }

        throw {
            status: data.status ?? fallback.status,
            code: data.code ?? fallback.code,
            message: data.message ?? fallback.message,
            path: data.path ?? fallback.path
        };
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

export function apiPatch(path, body) {
    return request(path, {
        method: "PATCH",
        body: JSON.stringify(body)
    });
}

export function apiDelete(path) {
    return request(path, {
        method: "DELETE"
    });
}


export function apiUpload(path, formData) {
    return request(path, {
        method: "POST",
        body: formData
    });
}