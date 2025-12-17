export async function loadHeader() {
    const html = await fetch("../components/header.html").then(r => r.text());
    document.body.insertAdjacentHTML("afterbegin", html);
}
