/**
 * Campus Study Hub — Automated Accessibility Checker
 *
 * Uses axe-core via Puppeteer to check WCAG AA compliance.
 *
 * Usage:
 *   node tools/run-axe.js [BASE_URL]
 *
 * Prerequisites:
 *   npm install puppeteer @axe-core/puppeteer
 */

const { AxePuppeteer } = require('@axe-core/puppeteer');
const puppeteer = require('puppeteer');

const BASE_URL = process.argv[2] || 'http://localhost:8080';

const PAGES = [
    '/login',
    '/dashboard',
    '/semesters',
    '/search',
    '/bookings/new',
];

(async () => {
    const browser = await puppeteer.launch({ headless: 'new' });

    console.log(`\n=== Campus Study Hub — Accessibility Report ===`);
    console.log(`Base URL: ${BASE_URL}\n`);

    let totalViolations = 0;

    for (const path of PAGES) {
        const url = `${BASE_URL}${path}`;
        const page = await browser.newPage();

        try {
            await page.goto(url, { waitUntil: 'networkidle2', timeout: 10000 });

            const results = await new AxePuppeteer(page)
                .withTags(['wcag2a', 'wcag2aa'])
                .analyze();

            const critical = results.violations.filter(v => v.impact === 'critical').length;
            const serious = results.violations.filter(v => v.impact === 'serious').length;
            const moderate = results.violations.filter(v => v.impact === 'moderate').length;
            const minor = results.violations.filter(v => v.impact === 'minor').length;

            console.log(`${path}`);
            console.log(`  Critical: ${critical}  Serious: ${serious}  Moderate: ${moderate}  Minor: ${minor}`);

            if (results.violations.length > 0) {
                results.violations.forEach(v => {
                    console.log(`  ⚠ [${v.impact}] ${v.id}: ${v.description}`);
                });
            }

            totalViolations += results.violations.length;
        } catch (err) {
            console.log(`${path}`);
            console.log(`  ❌ Error: ${err.message}`);
        }

        await page.close();
    }

    console.log(`\n=== Total violations: ${totalViolations} ===\n`);

    await browser.close();
    process.exit(totalViolations > 0 ? 1 : 0);
})();
