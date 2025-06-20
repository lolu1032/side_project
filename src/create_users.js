import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    vus: 1,
    iterations: 10000,
};

export default function () {
    let index = __ITER;
    let username = `user${index}`;  // 영문자+숫자만
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
