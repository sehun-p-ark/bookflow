const accommodations = [
    { id: 1, name: "힐링 캠핑장", address: "강원도 평창군" },
    { id: 2, name: "산속 글램핑", address: "경기도 가평" }
];

const list = document.getElementById("accommodation-list");

accommodations.forEach(acc => {
    const card = document.createElement("div");
    card.className = "card";

    card.innerHTML = `
        <h3>${acc.name}</h3>
        <p>${acc.address}</p>
        <a href="room.html?id=${acc.id}">방 보기</a>
    `;

    list.appendChild(card);
});
