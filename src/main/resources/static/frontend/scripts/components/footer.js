export async function loadFooter() {
    const html = await fetch("../components/footer.html").then(r => r.text());
    document.body.insertAdjacentHTML("beforeend", html);
}