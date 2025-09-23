import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,            // 동시에 요청을 보낼 가상 사용자 수
    duration: '30s',    // 테스트 지속 시간
};


export default function () {
    // 대상 API 엔드포인트
    const url = 'http://localhost/backend/api/play'; // 컨테이너/K8s 환경에 맞게 수정
    const res = http.post(url);

    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1); // 각 VU가 1초 대기 후 다시 요청
}