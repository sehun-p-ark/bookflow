export async function loadFooter() {
    const html = await fetch("/frontend/components/footer.html")
        .then(r => r.text());

    document.getElementById("footer").innerHTML = html;
}
