/**
 * Campus Study Hub — Smoke E2E Tests
 *
 * These tests verify core user flows are functional.
 * Run against a live dev/test environment.
 *
 * Environment Variables:
 *   TEST_API_BASE_URL  — Base URL of the app (default: http://localhost:8080)
 */

describe('Campus Study Hub — Smoke Tests', () => {

    it('should load the login page', () => {
        cy.visit('/login');
        cy.get('body').should('be.visible');
        cy.contains('Campus Study Hub');
    });

    it('should return healthy from actuator', () => {
        cy.request('/actuator/health').then((response) => {
            expect(response.status).to.eq(200);
            expect(response.body.status).to.eq('UP');
        });
    });

    it('should load the registration page', () => {
        cy.visit('/register');
        cy.get('body').should('be.visible');
        cy.get('form').should('exist');
    });

    it('should reject login with invalid credentials', () => {
        cy.visit('/login');
        cy.get('input[name="username"]').type('invalid@test.com');
        cy.get('input[name="password"]').type('wrongpassword');
        cy.get('form').submit();
        cy.url().should('include', '/login');
    });

    it('should load semester listing after login', () => {
        // Login with test admin credentials
        const adminEmail = Cypress.env('ADMIN_EMAIL') || 'admin@campus.com';
        const adminPassword = Cypress.env('ADMIN_PASSWORD') || 'admin123';

        cy.visit('/login');
        cy.get('input[name="username"]').type(adminEmail);
        cy.get('input[name="password"]').type(adminPassword);
        cy.get('form').submit();

        // Should redirect to dashboard
        cy.url().should('include', '/dashboard');
        cy.visit('/semesters');
        cy.contains('Semester');
    });

    it('should access the search page', () => {
        cy.visit('/search');
        cy.get('body').should('be.visible');
    });

    it('should access booking page when authenticated', () => {
        const adminEmail = Cypress.env('ADMIN_EMAIL') || 'admin@campus.com';
        const adminPassword = Cypress.env('ADMIN_PASSWORD') || 'admin123';

        cy.visit('/login');
        cy.get('input[name="username"]').type(adminEmail);
        cy.get('input[name="password"]').type(adminPassword);
        cy.get('form').submit();

        cy.visit('/bookings/new');
        cy.get('body').should('be.visible');
        cy.contains('Book');
    });
});
