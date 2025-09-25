import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
<<<<<<< HEAD
    vus: 30, // 동시에 요청을 보낼 가상 사용자 수
    duration: "30s", // 테스트 지속 시간
=======
    vus: 100, // 동시에 요청을 보낼 가상 사용자 수
    duration: "60s", // 테스트 지속 시간
>>>>>>> upstream/main
};

// Spring 백엔드에서 ProcessBuilder 실행 시 --env CATEGORY=... 로 주입
const category = __ENV.CATEGORY; // 환경변수로 category 전달받음

// 카테고리별 엔드포인트 설정
let url = "";
if (category === "docker") {
    url = "http://localhost/backend/api/play"; // Docker 서버 엔드포인트
} else if (category === "kubernetes") {
    url = "http://localhost/backend/api/play"; // Kubernetes 서버 엔드포인트
} else {
    url = "http://localhost/backend/api/play"; // 기본 로컬 fallback
}

export default function () {
    const res = http.post(url);

    // 응답 검증
    check(res, {
        "status is 200": (r) => r.status === 200,
    });

    sleep(1); // 각 VU가 1초 대기 후 다시 요청
}