# Progress

## Current Status

Status: Phase 7 transactions UI implemented

The project now has a complete expense management flow: users can add, view, search, filter, sort, edit, and delete expenses. The transactions screen observes real persisted data reactively, supports search by title/merchant/note, category filter chips, sort by date or amount, inline edit navigation, and delete with confirmation dialog.

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
- Added Hilt dependency injection with KSP annotation processing
- Created DatabaseModule, RepositoryModule, and UseCaseModule Hilt modules
- Created DashboardViewModel with @HiltViewModel consuming ObserveDashboardUseCase
- Created DashboardUiState with Loading, Empty, Error, and Loaded variants
- Rewrote DashboardScreen to use collectAsStateWithLifecycle() for lifecycle-safe collection
- Added budget progress color coding (green/amber/red) based on BudgetStatus
- Added accessibility semantics to progress bar, transaction rows, and category cards
- Fixed Money.format() to display रू symbol for NPR instead of raw currency code
- Fixed PulseCard redundant background paint
- Fixed PulseBottomBar to use NavigationBar instead of raw Row
- Fixed QuickAddRow to use LazyRow for horizontal scrolling on narrow screens
- Added bottom content padding to LazyColumn to avoid nav bar overlap
- Added TransactionRow merged semantics for screen reader coherence
- Migrated from kapt to KSP for both Room and Hilt annotation processing
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds with Hilt + KSP
- Implemented AddExpenseViewModel with @HiltViewModel consuming AddExpenseUseCase and CategorizeExpenseUseCase
- Built full Add Expense screen with amount input (NPR prefix, decimal keyboard), title, merchant, category chips, payment method chips, date, note, and recurring toggle
- Added debounced auto-categorization that suggests categories as user types merchant/title
- Category suggestion shows matched keyword and auto-selects if no manual selection made
- Added validation: amount required, title or merchant required, category required
- Save navigates back to dashboard on success via LaunchedEffect
- Wired AddExpenseUseCase and CategorizeExpenseUseCase in UseCaseModule
- Added kotlinx-coroutines-test dependency for ViewModel testing
- Added AddExpenseViewModelTest covering save validation, categorization, and success flow
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds
- Fixed multiple-dot amount input: rejects second dot, keeps previous valid value
- Fixed double-save race: early return if `isSaving` is already true
- Fixed date picker: added Material3 DatePickerDialog with clickable overlay on read-only field
- Fixed recurring toggle UX: shows "coming in a future update" hint when enabled
- Fixed suggestion row accessibility: added contentDescription for screen readers
- Removed unused `border` import from AddExpenseScreen
- Added amount input max-length cap (12 characters) to prevent overflow
- Expanded ViewModel tests: multiple-dot rejection, max-length cap, double-save guard, error clearing, Foodmandu categorization
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds after fixes
- Implemented TransactionsViewModel with @HiltViewModel consuming ObserveTransactionsUseCase and DeleteExpenseUseCase
- Built full Transactions screen with search bar, category filter chips (LazyRow), sort dropdown menu, and transaction list
- Transaction list items show merchant/title, category, amount, date, and delete icon
- Tapping a transaction navigates to edit (reuses AddExpenseScreen with pre-filled data)
- Delete shows AlertDialog confirmation before removing
- Added empty state with "No transactions found" / "No transactions yet" and clear filters action
- Added sort controls: Newest first, Oldest first, Highest amount, Lowest amount
- Updated AddExpenseViewModel to support edit mode via SavedStateHandle (loads existing expense by ID)
- Added EditExpense route with navArgument for expense ID
- Wired ObserveTransactionsUseCase, UpdateExpenseUseCase, and DeleteExpenseUseCase in UseCaseModule
- Added TransactionsViewModelTest with 9 tests covering load, search, filter, delete, clear filters, and empty state
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds

## In Progress

- Phase 8 analytics UI planning

## Not Started

- Production analytics charts (Compose Canvas donut/bar)
- Recurring expenses UI
- CSV export UI
- Dashboard ViewModel unit tests

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

Implement the Analytics UI:

1. Build AnalyticsViewModel consuming category spend data
2. Implement donut chart with Compose Canvas
3. Build category legend with amounts and percentages
4. Add period picker (this week, this month, last month)
5. Add spending/budget segmented control
6. Add accessible chart summaries
