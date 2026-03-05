import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Stress Test
 * 
 * Pushes the system beyond its expected capacity to see where it breaks.
 * Verifies that the system fails gracefully and recovers.
 */
export const options = {
    stages: [
        { duration: '2m', target: 50 },  // Normal load
        { duration: '2m', target: 100 }, // Stress
        { duration: '5m', target: 100 }, // Sustained Stress
        { duration: '2m', target: 200 }, // Breaking Point
        { duration: '5m', target: 200 }, // Sustained Breaking Point
        { duration: '2m', target: 0 },   // Recovery phase
    ],
    thresholds: {
        http_req_failed: ['rate<0.15'], // Allow up to 15% failure before hard fail
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Tenant-ID': 'stress-test-tenant',
        },
    };

    // Hit critical endpoints
    const responses = http.batch([
        ['GET', `${BASE_URL}/`, null, params],
        ['GET', `${BASE_URL}/api/v1/rooms`, null, params],
        ['GET', `${BASE_URL}/dashboard`, null, params],
    ]);

    responses.forEach((res) => {
        check(res, {
            'is status 200 or 429': (r) => r.status === 200 || r.status === 429,
        });
    });

    sleep(0.5); // Fast requests
}
