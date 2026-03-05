import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Smoke Test
 * 
 * Verifies that the application can handle a minimal load without errors.
 * This is the first test to run after a deployment.
 */
export const options = {
    vus: 3, // Very low number of users
    duration: '1m', // Short duration
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
        http_req_failed: ['rate<0.01'],   // Less than 1% errors
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const responses = http.batch([
        ['GET', `${BASE_URL}/`],
        ['GET', `${BASE_URL}/login`],
        ['GET', `${BASE_URL}/actuator/health`],
    ]);

    responses.forEach((res) => {
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });

    sleep(1);
}
