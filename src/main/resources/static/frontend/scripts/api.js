export const API = "http://localhost:8080/api";

export async function apiGet(path) {
    const res = await fetch(`${API}${path}`);
    return res.json();
}

export async function apiPost(path, body) {
    const res = await fetch(`${API}${path}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });
    return res.json();
}
