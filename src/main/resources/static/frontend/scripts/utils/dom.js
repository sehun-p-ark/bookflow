export function qs(selector) {
    return document.querySelector(selector);
}

export function qsa(selector) {
    return document.querySelectorAll(selector);
}

export function create(tag, className) {
    const el = document.createElement(tag);
    if (className) el.classList.add(className);
    return el;
}
