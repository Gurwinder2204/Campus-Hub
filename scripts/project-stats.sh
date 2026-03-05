#!/bin/sh
# ============================================================
# Campus Study Hub — Project Statistics
# ============================================================
# Outputs code metrics for the project.
#
# Usage:
#   ./scripts/project-stats.sh
# ============================================================

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

echo "╔══════════════════════════════════════════════╗"
echo "║     Campus Study Hub — Project Statistics     ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# Lines of code
echo "📊 Lines of Code"
echo "─────────────────────────────────────────────"

JAVA_LOC=$(find "$PROJECT_DIR/src" -name "*.java" -exec cat {} + 2>/dev/null | wc -l)
echo "  Java:           $JAVA_LOC lines"

HTML_LOC=$(find "$PROJECT_DIR/src" -name "*.html" -exec cat {} + 2>/dev/null | wc -l)
echo "  HTML (Thymeleaf):$HTML_LOC lines"

SQL_LOC=$(find "$PROJECT_DIR/src" -name "*.sql" -exec cat {} + 2>/dev/null | wc -l)
echo "  SQL:            $SQL_LOC lines"

PROPS_LOC=$(find "$PROJECT_DIR/src" -name "*.properties" -exec cat {} + 2>/dev/null | wc -l)
echo "  Properties:     $PROPS_LOC lines"

TOTAL_LOC=$((JAVA_LOC + HTML_LOC + SQL_LOC + PROPS_LOC))
echo "  ────────────────────────────"
echo "  Total:          $TOTAL_LOC lines"
echo ""

# API counts
echo "🔌 APIs"
echo "─────────────────────────────────────────────"

REST_CONTROLLERS=$(grep -rl "@RestController" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  REST Controllers: $REST_CONTROLLERS"

MVC_CONTROLLERS=$(grep -rl "@Controller" "$PROJECT_DIR/src/main" 2>/dev/null | grep -v RestController | wc -l)
echo "  MVC Controllers:  $MVC_CONTROLLERS"

REST_ENDPOINTS=$(grep -r "@\(Get\|Post\|Put\|Patch\|Delete\)Mapping" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  Total Endpoints:  $REST_ENDPOINTS"
echo ""

# Database
echo "🗄️ Database"
echo "─────────────────────────────────────────────"

MIGRATIONS=$(find "$PROJECT_DIR/src/main/resources/db/migration" -name "*.sql" 2>/dev/null | wc -l)
echo "  Flyway Migrations: $MIGRATIONS"

ENTITIES=$(grep -rl "@Entity" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  JPA Entities:      $ENTITIES"

REPOS=$(grep -rl "extends JpaRepository\|extends CrudRepository" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  Repositories:      $REPOS"
echo ""

# Services & Components
echo "⚙️ Services & Components"
echo "─────────────────────────────────────────────"

SERVICES=$(grep -rl "@Service" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  Services:     $SERVICES"

CONFIG=$(grep -rl "@Configuration" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  Configs:      $CONFIG"

COMPONENTS=$(grep -rl "@Component" "$PROJECT_DIR/src/main" 2>/dev/null | wc -l)
echo "  Components:   $COMPONENTS"

TEMPLATES=$(find "$PROJECT_DIR/src/main/resources/templates" -name "*.html" 2>/dev/null | wc -l)
echo "  Templates:    $TEMPLATES"
echo ""

# Tests
echo "🧪 Tests"
echo "─────────────────────────────────────────────"

TEST_FILES=$(find "$PROJECT_DIR/src/test" -name "*Test*.java" 2>/dev/null | wc -l)
echo "  Test Files:   $TEST_FILES"

E2E_TESTS=$(find "$PROJECT_DIR/frontend/e2e" -name "*.cy.js" 2>/dev/null | wc -l)
echo "  Cypress E2E:  $E2E_TESTS"

LOAD_TESTS=$(find "$PROJECT_DIR/scripts/load-tests" -name "*.js" 2>/dev/null | wc -l)
echo "  Load Tests:   $LOAD_TESTS"
echo ""

echo "╔══════════════════════════════════════════════╗"
echo "║             Statistics Complete!              ║"
echo "╚══════════════════════════════════════════════╝"
