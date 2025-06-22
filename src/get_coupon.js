import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 3000, // 동시 유저 수
    duration: '1s', // 1초에 몰아넣기
};

export default function () {
    const userId = __VU - 1;

    const payload = JSON.stringify({
        id: 20,  // 쿠폰 ID
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
