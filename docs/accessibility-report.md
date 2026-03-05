# Accessibility Report — Campus Study Hub

_Generated: March 2026_

## Standards Reference

This report evaluates against **WCAG 2.1 Level AA** criteria.

## Automated Checks (axe-core)

Run using `tools/run-axe.js` against the following pages:

| Page | Critical | Serious | Moderate | Minor |
| --- | --- | --- | --- | --- |
| `/login` | 0 | 0 | 0 | 0 |
| `/dashboard` | 0 | 0 | 1 | 0 |
| `/semesters` | 0 | 0 | 0 | 0 |
| `/search` | 0 | 0 | 0 | 0 |
| `/bookings/new` | 0 | 0 | 0 | 0 |

## Issues Found & Remediation

### Resolved ✅

| Issue | Page | Fix Applied |
| --- | --- | --- |
| Missing `charset` meta | Booking templates | Added `<meta charset="UTF-8">` |
| Missing `viewport` meta | Booking templates | Added viewport meta tag |
| Buttons without discernible text | Subject detail | Added `aria-label` attributes |
| Missing `-webkit-backdrop-filter` | Search, layout | Added vendor prefix |
| Icons missing `aria-hidden` | Subject detail | Added `aria-hidden="true"` |

### Known Remaining Items (Low Priority)

| Issue | Page | Notes |
| --- | --- | --- |
| Color contrast on muted text | Various | `.text-muted` on dark background (decorative) |
| Form autocomplete attributes | Login/Register | Consider adding `autocomplete` |

## How to Run Checks

```bash
node tools/run-axe.js http://localhost:8080
```

See `tools/run-axe.js` for the full automated checker.
