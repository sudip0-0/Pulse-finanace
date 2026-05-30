# Tasks

## Phase 1 - Project Foundation

- [x] Create Android project structure
- [x] Configure Kotlin, Compose, and Material 3
- [x] Add package structure for presentation, domain, data, and DI
- [x] Add navigation graph
- [x] Add app theme matching `Design.md`
- [x] Add base typography, color, spacing, and shape tokens
- [x] Add common UI components
- [x] Add sample preview data using Nepal-specific merchants

## Phase 2 - Local Database

- [x] Add Room dependencies
- [x] Create `PulseDatabase`
- [x] Create `ExpenseEntity`
- [x] Create `CategoryEntity`
- [x] Create `CategoryKeywordEntity`
- [x] Create `RecurringRuleEntity`
- [x] Create `BudgetEntity`
- [x] Create DAOs
- [x] Add type converters for dates, instants, and year-month values
- [x] Seed default categories
- [x] Seed Nepal-specific category keywords
- [x] Add database migration strategy

## Phase 3 - Domain Layer

- [x] Create domain money model using minor units
- [x] Create expense domain model
- [x] Create category domain model
- [x] Create recurring rule domain model
- [x] Define repository interfaces
- [x] Implement `CategorizeExpenseUseCase`
- [x] Implement `AddExpenseUseCase`
- [x] Implement `UpdateExpenseUseCase`
- [x] Implement `DeleteExpenseUseCase`
- [x] Implement `ObserveDashboardUseCase`
- [x] Implement `ObserveTransactionsUseCase`
- [x] Implement `CalculateBudgetProgressUseCase`
- [x] Implement `GenerateDueRecurringExpensesUseCase`
- [x] Implement `ExportTransactionsCsvUseCase`

## Phase 4 - Data Repositories

- [x] Implement expense repository
- [x] Implement category repository
- [x] Implement budget repository
- [x] Implement recurring expense repository
- [x] Implement category keyword repository
- [x] Add entity-domain mappers
- [x] Ensure all read queries use `Flow` where the UI should update reactively

## Phase 5 - Dashboard UI

- [x] Build dashboard header
- [x] Build monthly spend card
- [x] Build add expense CTA
- [x] Build budget progress card
- [x] Build top category cards
- [x] Build quick add row
- [x] Wire quick add shortcuts to prefilled Add Expense
- [x] Build bottom navigation
- [x] Connect dashboard to real ViewModel state
- [x] Add loading and empty states
- [x] Add Hilt dependency injection
- [x] Wire DI modules (Database, Repository, UseCase)
- [x] Add accessibility semantics to progress bar and transaction rows
- [x] Fix NPR display to use रू symbol

## Phase 6 - Add Expense UI

- [x] Build amount input
- [x] Build merchant/title input
- [x] Build category selector
- [x] Show auto-category suggestion
- [x] Build date selector with DatePickerDialog
- [x] Build payment method selector
- [x] Build recurring toggle with placeholder hint
- [x] Build note field
- [x] Save expense to database
- [x] Validate required fields
- [x] Return to dashboard after save
- [x] Wire AddExpenseUseCase and CategorizeExpenseUseCase through Hilt
- [x] Add ViewModel unit tests for save flow and categorization
- [x] Fix multiple-dot amount input rejection
- [x] Fix double-save guard
- [x] Fix date picker (was read-only with no way to change)
- [x] Fix suggestion row accessibility
- [x] Add amount max-length cap

## Phase 7 - Transactions UI

- [x] Build transaction list
- [x] Add search (with debounce)
- [x] Add category filter
- [x] Add date range filter (via TransactionFilters support)
- [x] Add sort controls (reactive re-sort without re-query)
- [x] Add edit expense flow
- [x] Add delete confirmation
- [x] Add empty search state
- [x] Wire ObserveTransactionsUseCase, UpdateExpenseUseCase, DeleteExpenseUseCase through Hilt
- [x] Add edit route with expense ID navigation argument
- [x] Add TransactionsViewModel unit tests

## Phase 8 - Analytics UI

- [x] Build analytics top app bar
- [x] Build period picker (This month, Last month)
- [x] Implement donut chart with Compose Canvas
- [x] Build category legend with color, name, amount, and percentage
- [x] Build recent transactions section
- [x] Animate chart changes (tween animation on donut sweep)
- [x] Add accessible chart summaries
- [x] Add empty analytics state
- [x] Wire AnalyticsViewModel with ObserveDashboardUseCase
- [x] Add AnalyticsViewModel unit tests (aggregation, percentages, sweep angles, accessibility)

## Phase 9 - Recurring Expenses

- [x] Build recurring expense list
- [x] Build recurring expense create/edit flow
- [x] Generate due expenses on app start
- [x] Prevent duplicate recurring generation
- [x] Allow pausing recurring rules
- [x] Add next due date display

## Phase 10 - Settings and Export

- [x] Build settings screen
- [x] Add monthly budget editor
- [x] Add currency display with NPR default
- [x] Remove notification placeholder from MVP settings
- [x] Add recurring expenses entry point
- [x] Add category management entry point
- [x] Implement minimal category management screen
- [x] Implement CSV export
- [x] Verify CSV handles Nepali Unicode text

## Phase 11 - Testing

- [x] Add categorization unit tests
- [x] Add money formatting unit tests
- [x] Add budget calculation unit tests
- [x] Add recurring schedule unit tests
- [x] Add CSV escaping unit tests
- [x] Add Room DAO tests
- [x] Add explicit first data slice categorization tests for Daraz and NEA
- [x] Add dashboard ViewModel tests
- [x] Add add-expense UI test
- [ ] Run add-expense UI test on emulator/device

## Phase 13 - Receipt Scan

- [x] Add ML Kit on-device text recognition dependency
- [x] Add camera and gallery permissions with FileProvider
- [x] Implement `ReceiptTextParser` with Nepal receipt heuristics
- [x] Implement `ParseReceiptFromTextUseCase` and `MlKitReceiptTextRecognizer`
- [x] Build scan flow (capture, processing, review) with category suggestion
- [x] Wire `ScanReceipt` navigation from Add Expense
- [x] Add parser and ViewModel unit tests
- [ ] Manual smoke test on device (camera + gallery + save)

## Phase 12 - Portfolio Polish

- [ ] Add README screenshots
- [ ] Add architecture diagram
- [ ] Add sample data generator
- [ ] Add demo GIF or short screen recording
- [ ] Add recruiter-facing feature summary
- [ ] Add known limitations section based on actual implementation
