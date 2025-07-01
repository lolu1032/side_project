import http from 'k6/http';
import { sleep } from 'k6';

const START_INDEX = 3724; // 시작 번호
export let options = {
    vus: 1,
    iterations: 3000, // 3000명 생성
};

export default function () {
    let index = START_INDEX + __ITER;
    let username = `user${index}`;  // 영문자+숫자
    let password = 'password123@';

    let payload = JSON.stringify({
        username: username,
        password: password,
    });

    let headers = { 'Content-Type': 'application/json' };

    let res = http.post('http://localhost:8080/api/signup', payload, { headers: headers });
    console.log(`Created user: ${username} (status: ${res.status})`);

    sleep(0.1);
}
