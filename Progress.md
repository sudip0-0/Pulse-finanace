# Progress

## Current Status

Status: Phase 4 data repositories implemented

The project now has a buildable Android app foundation, Room local persistence, a pure Kotlin domain layer, and concrete data repository implementations that bridge Room DAOs to domain repository interfaces. Entity-domain mappers handle Money composition, enum translation, and null-safe payment method mapping. All reactive reads use Flow with IO dispatcher offloading.

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
- Hardened money formatting to use integer-only formatting and reject unformattable `Long.MIN_VALUE`
- Improved categorization for merchant suffixes like `Pathao ride` and sort-order tie-breaking without seed-ID priority coupling
- Added recurring schedule coverage for month-end rules, inactive rules, and end-date boundaries
- Added future-date and update/delete validation coverage
- Implemented entity-domain mappers for expenses, categories, budgets, recurring rules, and category keywords
- Implemented `ExpenseRepositoryImpl` backed by `ExpenseDao` with filtered query support
- Implemented `CategoryRepositoryImpl` backed by `CategoryDao`
- Implemented `BudgetRepositoryImpl` backed by `BudgetDao` with upsert logic
- Implemented `RecurringRuleRepositoryImpl` backed by `RecurringRuleDao`
- Implemented `CategoryKeywordRepositoryImpl` backed by `CategoryKeywordDao`
- Added DAO queries for recurring duplicate detection, previous merchant category lookup, and filtered transactions
- All repository reads use `Flow` with `flowOn(Dispatchers.IO)` for off-main-thread execution
- All repository writes use `withContext(Dispatchers.IO)` for suspend safety
- Added mapper round-trip unit tests for all five entity-domain pairs
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds

## In Progress

- Phase 5 dashboard UI planning

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

Implement the dashboard UI:

1. Wire repositories through dependency injection (Hilt modules)
2. Build DashboardViewModel consuming ObserveDashboardUseCase
3. Connect Compose dashboard screen to real ViewModel state
4. Add loading and empty states
5. Verify reactive updates when expenses are added
