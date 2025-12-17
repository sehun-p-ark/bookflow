import { loadHeader } from "../components/header.js";
import { loadFooter } from "../components/footer.js";

loadHeader();
loadFooter();

const introText = document.getElementById('intro-text');

if (!introText) {
    console.error('intro-text 요소를 찾을 수 없습니다.');
} else {
    introText.textContent = '캠핑 / 숙소 예약 서비스 BookFlow입니다.';
}
