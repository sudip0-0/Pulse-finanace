# Progress

## Current Status

Status: Phase 3 domain layer implemented

The project now has a buildable Android app foundation, Room local persistence, and a pure Kotlin domain layer for the Nepal-focused Pulse MVP. The domain layer defines finance models, repository contracts, deterministic categorization, budget calculations, recurring generation, transaction observation entry points, and CSV export preparation.

## Completed

- Defined product concept
- Defined Nepal-specific market positioning
- Defined core MVP features
- Defined recommended Android tech stack
- Defined Clean Architecture structure
- Defined Room database model
- Defined Nepal-specific auto-categorization keywords
- Defined dark fintech UI direction based on the provided mockup
- Defined initial implementation task backlog
- Defined known risks and limitations
- Created Android project scaffold with a single `:app` module
- Configured Kotlin, Jetpack Compose, Material 3, and Gradle wrapper
- Added Clean Architecture package boundaries for presentation, domain, data, and DI
- Added navigation routes for Dashboard, Analytics, Transactions, Add Expense, Recurring, and Settings
- Added dark theme color, typography, spacing, and shape tokens from `Design.md`
- Added shared UI components for cards, category rows, transactions, and floating bottom navigation
- Added Nepal-specific sample merchants including Pathao, Foodmandu, Daraz, Bhat-Bhateni, eSewa, Khalti, NTC, Ncell, NEA, WorldLink, and Vianet
- Verified `.\gradlew.bat :app:assembleDebug` succeeds
- Added Room dependencies, compiler setup, and exported schema configuration
- Created `PulseDatabase`
- Created Room entities for expenses, categories, category keywords, recurring rules, and budgets
- Created DAOs for CRUD, date range queries, category spend aggregation, budget lookup, recurring rules, and keyword lookup
- Added converters for `LocalDate`, `Instant`, and `YearMonth`
- Seeded deterministic default categories and Nepal-specific keywords
- Added migration holder for future Room schema versions
- Added converter unit tests and instrumented database/DAO tests
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebugAndroidTest` succeeds
- Hardened DAO insert conflict policies to avoid accidental replace/delete behavior
- Added composite indexes for date-sorted dashboard queries and category aggregation
- Added enum-backed storage for keyword match types and recurring frequencies
- Made keyword matching case-insensitive and locale-aware at the unique index level
- Added immutable domain models for money, expenses, categories, category keywords, budgets, and recurring rules
- Added repository interfaces for expenses, categories, budgets, recurring rules, and category keywords
- Implemented add, update, delete, dashboard observation, transaction observation, categorization, budget progress, recurring generation, and CSV export use cases
- Added deterministic categorization priority: exact merchant, strong keyword, previous merchant category, weak keyword, Other
- Added unit tests for money formatting, categorization, budget progress, recurring generation, CSV escaping, and expense validation
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds

## In Progress

- Phase 4 data repository planning

## Not Started

- Expense CRUD
- Real dashboard state from persisted expenses
- Production analytics charts
- Recurring expenses
- CSV export
- Broader domain and UI tests

## Decisions Made

- The app will target Android first.
- The app will use Jetpack Compose.
- The app will be offline-first for the MVP.
- The app will use Room for local persistence.
- The app will use deterministic auto-categorization rules instead of ML for the MVP.
- The default currency will be NPR.
- UI direction will follow a dark premium fintech style.
- Nepal-specific merchants and services will be included in sample data and categorization.

## Open Decisions

- Whether to support Bikram Sambat dates in MVP or post-MVP
- Whether to include income tracking in MVP
- Whether to include account/payment-source tracking
- Whether to use custom Canvas charts or a Compose chart library
- Whether to support biometric app lock
- Whether to support CSV import as well as export

## Next Recommended Step

Implement the data repository bridge:

1. Entity-domain mappers
2. Repository implementations backed by Room DAOs
3. Dependency injection wiring
4. Connect Add Expense and Dashboard to real data
5. ViewModel tests for reactive dashboard updates
