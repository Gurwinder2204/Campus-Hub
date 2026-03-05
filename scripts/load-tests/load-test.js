import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Load Test
 * 
 * Simulates realistic traffic by ramping up users to a target amount.
 * Used to measure p95/p99 response times and general stability.
 */
export const options = {
    stages: [
        { duration: '2m', target: 20 }, // Ramp-up to 20 users
        { duration: '5m', target: 20 }, // Stay at 20 users
        { duration: '2m', target: 50 }, // Ramp-up to 50 users
        { duration: '5m', target: 50 }, // Stay at 50 users
        { duration: '2m', target: 0 },  // Ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'], // 95% of requests should be < 1s
        http_req_failed: ['rate<0.05'],    // Less than 5% errors
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    // Simulate common user path: Home -> Login -> Health check
    let res = http.get(`${BASE_URL}/`);
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);

    res = http.get(`${BASE_URL}/login`);
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(2);

    res = http.get(`${BASE_URL}/actuator/health`);
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(3);
}
