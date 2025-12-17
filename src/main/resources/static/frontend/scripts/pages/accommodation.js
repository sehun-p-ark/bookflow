const accommodations = [
    { id: 1, name: '파인 캠핑장', address: '강원도', price: 80000 },
    { id: 2, name: '오션뷰 숙소', address: '부산', price: 120000 }
];

const list = document.getElementById('accommodation-list');

if (!list) {
    console.error('accommodation-list 요소를 찾을 수 없습니다.');
}

list.innerHTML = accommodations.map(a => `
    <div class="card">
        <h3>${a.name}</h3>
        <p>${a.address}</p>
        <p>${a.price.toLocaleString()}원</p>
        <a href="/frontend/pages/room.html?accommodationId=${a.id}">
            방 보기
        </a>
    </div>
`).join('');
