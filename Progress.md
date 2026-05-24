# Progress

## Current Status

Status: MVP hardening in progress

The project now has 156 unit tests covering all critical finance, categorization, persistence, and UI behavior. Test coverage spans money formatting, Nepal-specific auto-categorization, budget calculations, recurring schedule generation with duplicate prevention, CSV export with Unicode, ViewModel layers, category management, and data mappers/converters. A Compose add-expense UI test has been added and the Android test APK compiles; executing it still requires an emulator or device.

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
- Implemented AnalyticsViewModel with @HiltViewModel consuming ObserveDashboardUseCase with period selection
- Built full Analytics screen with Compose Canvas donut chart, animated sweep angles, center total spend
- Added period picker dropdown (This week, This month, Last month)
- Added Spending/Budget segmented control with pill-style selection
- Built category legend with color dots, names, amounts, and percentages
- Built recent transactions section
- Added accessible chart summary via semantics contentDescription
- Added empty state "Add expenses to see insights"
- Donut chart uses rounded stroke caps and gap between segments
- Animation uses tween(600ms) for smooth entry
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds with analytics
- Implemented RecurringViewModel with @HiltViewModel consuming RecurringRuleRepository and CategoryRepository
- Built full Recurring Expenses list screen with FAB, pause/resume toggle, delete confirmation, next due date display
- Built Add/Edit Recurring Rule screen with amount, title, merchant, category chips, frequency selector, interval, start/end date pickers
- Added navigation routes for AddRecurringRule and EditRecurringRule with navArgument for rule ID
- Wired GenerateDueRecurringExpensesUseCase in UseCaseModule and triggered on app start via DashboardViewModel
- Extended RecurringRuleRepository interface with observeAllRules(), getRuleById(), and deleteRule()
- Updated RecurringRuleRepositoryImpl with new methods
- Duplicate prevention via existing hasGeneratedExpenseForRecurringRule() check in GenerateDueRecurringExpensesUseCase
- Paused rules shown with reduced opacity and "Paused" label; overdue rules shown in red
- Nepal-specific suggestions shown in create form (Rent, WorldLink, NEA, Khanepani, NTC/Ncell, School fee)
- Added RecurringViewModelTest (6 tests: load, empty, pause, resume, delete, overdue label)
- Added AddRecurringRuleViewModelTest (9 tests: categories, validation, save, dots, frequency, interval, edit mode)
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` succeeds with recurring expenses
- Implemented SettingsViewModel with @HiltViewModel consuming BudgetRepository, ExpenseRepository, and ExportTransactionsCsvUseCase
- Rewrote SettingsScreen with dark rounded-row style matching Design.md
- Added monthly budget editor via AlertDialog with NPR amount input and validation
- Added currency display showing "NPR (रू)" — read-only since multi-currency is not implemented
- Initially added a disabled notification settings row
- Added recurring expenses entry point navigating to RecurringScreen
- Initially added a categories entry point for default category viewing
- Implemented CSV export using Android CreateDocument API (no storage permissions needed)
- CSV export writes UTF-8 with BOM for Excel compatibility with Nepali Unicode
- Export covers current month's expenses with all planned columns (Date, Title, Merchant, Category, Amount, Currency, Payment Method, Note, Recurring)
- Wired ExportTransactionsCsvUseCase in UseCaseModule
- Added SettingsViewModelTest (7 tests: budget load, not set, validation, save, export ready, export complete, currency label)
- Expanded ExportTransactionsCsvUseCaseTest to 8 tests: escaping, header, Nepali Unicode, recurring column, non-recurring, amount format, empty fields, multiple rows
- Verified `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest` succeeds with settings and export
- Fixed budget dialog dismiss race: uses budgetSaved flag with LaunchedEffect instead of synchronous error check
- Fixed CSV line endings: uses CRLF (\r\n) per RFC 4180 for Excel/Windows compatibility
- Fixed writeCsvToUri: returns boolean, shows failure toast on IOException or null stream
- Fixed export double-tap: guards against concurrent export with early return
- Added usesCrlfLineEndings test to verify RFC 4180 compliance
- Implemented DashboardViewModelTest (10 tests: loading, monthly spend, empty state, budget progress, no budget, top 3 categories, percentage, merchant/title display, month label, quick add items)
- Expanded MoneyTest from 5 to 16 tests: zero, subtraction, comparison, isPositive, currency mismatch, small amounts, typical Nepal expenses, blank currency
- Expanded CalculateBudgetProgressUseCaseTest from 2 to 8 tests: under, warning, danger, over budget, zero spend, zero budget, currency mismatch, typical Nepal scenario
- Expanded CategorizeExpenseUseCaseTest from 7 to 13 tests: added Nepal-specific merchants (eSewa, Khalti, Bhat-Bhateni, NTC, WorldLink, Foodmandu)
- Total test count: 147 unit tests across 20 test suites, all passing
- Verified `.\gradlew.bat :app:testDebugUnitTest` succeeds with full Phase 11 coverage
- Added explicit categorization unit tests for Daraz -> Shopping and NEA -> Utilities to complete the first real data slice merchant examples
- Verified `.\gradlew.bat :app:testDebugUnitTest` succeeds with 149 JVM unit tests
- Verified `.\gradlew.bat :app:assembleDebugAndroidTest` succeeds for Room DAO/instrumented test compilation
- Wired dashboard Quick Add chips to Add Expense with Nepal-relevant prefilled merchant/category values
- Wired dashboard search icon to the Transactions screen and removed the inert notification icon from the dashboard header
- Removed the disabled notification placeholder from Settings
- Implemented a minimal Categories screen from Settings for viewing defaults, adding custom categories, editing custom categories, and archiving custom categories
- Added CategoriesViewModel tests for load, add, edit, duplicate rejection, default protection, and archive behavior
- Added AddExpenseViewModel coverage for Quick Add prefill state
- Added a Compose add-expense UI test covering dashboard -> add expense -> save -> dashboard refresh from persisted data
- Verified `.\gradlew.bat :app:testDebugUnitTest` succeeds with 156 JVM unit tests
- Verified `.\gradlew.bat :app:assembleDebugAndroidTest` succeeds with the new Compose UI test compiled
- Senior code review pass (no behaviour changes besides those listed below):
  - Fixed CSV `Amount` column to write a plain numeric value (`450.50`) instead of the formatted display string (`रू 450.50`); the `Currency` column already covered `NPR`. Spreadsheets can now parse the column as a number.
  - Fixed Material3 DatePicker timezone handling on AddExpense and AddRecurringRule: convert UTC midnight millis through `ZoneOffset.UTC` to a `LocalDate` rather than dividing by 86_400_000L. Eliminates a possible day shift around midnight UTC.
  - Made `ExpenseDao.findPreviousCategoryIdForMerchant` case-insensitive (`LOWER(merchant) = LOWER(...)`), so re-using the same merchant typed in a different case keeps the previous category.
  - Switched `TransactionsViewModel` amount sort to use the underlying `amountMinor` field instead of parsing the formatted display string.
  - Populated category name and color on dashboard and analytics recent transaction rows (previously sent as empty strings); `DashboardSnapshot` now carries the active category list.
  - Hid the floating bottom navigation on modal screens (Add/Edit Expense, Add/Edit Recurring, Categories) so the surface is no longer rendered with no item selected.
  - Removed the inert `Recurring expense` toggle from Add Expense; recurring rules are managed in Settings → Recurring. `Switch`/`SwitchDefaults` imports and `isRecurring` state dropped accordingly.
  - Removed the dead `Spending`/`Budget` segmented control from Analytics — only `Spending` was ever rendered and the toggle never branched the UI.
  - Removed the duplicate `Add category` button from Categories; the TopAppBar `+` action remains the single entry point.
  - Set `android:allowBackup="false"` in `AndroidManifest.xml`. The MVP is offline and privacy-respecting; the SQLite database should not auto-backup to Google Drive without an explicit opt-in.
  - Removed unused DAO methods: `ExpenseDao.delete(entity)`, `ExpenseDao.getExpensesBetween` (suspend), `ExpenseDao.getCategorySpendingBetween` (suspend) — only the Flow + `deleteById` versions are used.
  - Deleted unused presentation code: `presentation/preview/PulsePreviewData.kt` (entire package), `presentation/common/components/TransactionRow.kt`, `presentation/common/components/CategorySpendCard.kt`. Each screen reimplements its own row with category-coloured leading dots and that idiom is more flexible.
  - Removed seven `.gitkeep` files left over from initial scaffolding alongside real Kotlin sources.
  - Updated Tasks.md Phase 8 to drop the inaccurate `This week` and Spending/Budget segmented control claims.
  - Updated `ExportTransactionsCsvUseCaseTest.amountFormattedAsPlainNumberAndCurrencyColumnSeparate` to assert the corrected behaviour.
- Verified `.\gradlew.bat :app:testDebugUnitTest` succeeds (156 JVM unit tests) after the senior review pass
- Verified `.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest` succeeds after the senior review pass

## In Progress

- MVP hardening and device verification

## Not Started

- Running the add-expense UI test on a connected emulator/device

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

Run the add-expense UI test when an emulator or device is available:

1. Open the dashboard
2. Add a Nepal-relevant expense
3. Verify the dashboard total and recent transaction list update from persisted Room data
