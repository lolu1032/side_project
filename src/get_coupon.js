import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '3s', target: 3000 }, // 3초 동안 점진적으로 3000명 증가
        { duration: '5s', target: 3000 }, // 5초 동안 유지
        { duration: '3s', target: 0 },    // 3초 동안 점진적으로 줄이기
    ],
};

export default function () {
    const userId = __VU - 1;

    const payload = JSON.stringify({
        id: 62,
        userId: userId,
    });

    const headers = { 'Content-Type': 'application/json' };

    let res = http.post('http://localhost:8080/api/get', payload, { headers: headers });

    check(res, {
        'status was 200 or 500': (r) => r.status === 200 || r.status === 500,
    });

    console.log(`User ${userId} tried to get coupon - status: ${res.status}`);
    sleep(1);
}
