const params = new URLSearchParams(window.location.search);
const accId = params.get("id");

document.getElementById("accommodation-info").innerHTML = `
    <h2>숙소 ID: ${accId}</h2>
    <p>숙소 상세 정보는 추후 API로 제공됩니다.</p>
`;

const rooms = [
    { id: 1, name: "A사이트", price: 50000, capacity: 4 },
    { id: 2, name: "B사이트", price: 60000, capacity: 6 }
];

const list = document.getElementById("room-list");

rooms.forEach(room => {
    const box = document.createElement("div");
    box.className = "card";

    box.innerHTML = `
        <h4>${room.name}</h4>
        <p>가격: ${room.price}원</p>
        <p>수용 인원: ${room.capacity}명</p>
        <a href="reservation.html?roomId=${room.id}">예약하기</a>
    `;

    list.appendChild(box);
});
