const { defineConfig } = require('cypress');

module.exports = defineConfig({
    e2e: {
        baseUrl: process.env.TEST_API_BASE_URL || 'http://localhost:8080',
        supportFile: false,
        specPattern: 'frontend/e2e/**/*.cy.js',
        video: false,
        screenshotOnRunFailure: true,
    },
});
