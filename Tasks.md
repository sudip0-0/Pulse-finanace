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

- [ ] Create domain money model using minor units
- [ ] Create expense domain model
- [ ] Create category domain model
- [ ] Create recurring rule domain model
- [ ] Define repository interfaces
- [ ] Implement `CategorizeExpenseUseCase`
- [ ] Implement `AddExpenseUseCase`
- [ ] Implement `UpdateExpenseUseCase`
- [ ] Implement `DeleteExpenseUseCase`
- [ ] Implement `ObserveDashboardUseCase`
- [ ] Implement `ObserveTransactionsUseCase`
- [ ] Implement `CalculateBudgetProgressUseCase`
- [ ] Implement `GenerateDueRecurringExpensesUseCase`
- [ ] Implement `ExportTransactionsCsvUseCase`

## Phase 4 - Data Repositories

- [ ] Implement expense repository
- [ ] Implement category repository
- [ ] Implement budget repository
- [ ] Implement recurring expense repository
- [ ] Add entity-domain mappers
- [ ] Ensure all read queries use `Flow` where the UI should update reactively

## Phase 5 - Dashboard UI

- [ ] Build dashboard header
- [ ] Build monthly spend card
- [ ] Build add expense CTA
- [ ] Build budget progress card
- [ ] Build top category cards
- [ ] Build quick add row
- [ ] Build bottom navigation
- [ ] Connect dashboard to real ViewModel state
- [ ] Add loading and empty states

## Phase 6 - Add Expense UI

- [ ] Build amount input
- [ ] Build merchant/title input
- [ ] Build category selector
- [ ] Show auto-category suggestion
- [ ] Build date selector
- [ ] Build payment method selector
- [ ] Build recurring toggle
- [ ] Build note field
- [ ] Save expense to database
- [ ] Validate required fields
- [ ] Return to dashboard after save

## Phase 7 - Transactions UI

- [ ] Build transaction list
- [ ] Add search
- [ ] Add category filter
- [ ] Add date range filter
- [ ] Add sort controls
- [ ] Add edit expense flow
- [ ] Add delete confirmation
- [ ] Add empty search state

## Phase 8 - Analytics UI

- [ ] Build analytics top app bar
- [ ] Build period picker
- [ ] Build Spending/Budget segmented control
- [ ] Implement donut chart with Compose Canvas
- [ ] Implement bar chart with Compose Canvas
- [ ] Build category legend
- [ ] Build recent transactions section
- [ ] Animate chart changes
- [ ] Add accessible chart summaries

## Phase 9 - Recurring Expenses

- [ ] Build recurring expense list
- [ ] Build recurring expense create/edit flow
- [ ] Generate due expenses on app start
- [ ] Prevent duplicate recurring generation
- [ ] Allow pausing recurring rules
- [ ] Add next due date display

## Phase 10 - Settings and Export

- [ ] Build settings screen
- [ ] Add monthly budget editor
- [ ] Add currency display with NPR default
- [ ] Add notification toggle placeholder
- [ ] Add recurring expenses entry point
- [ ] Add category management entry point
- [ ] Implement CSV export
- [ ] Verify CSV handles Nepali Unicode text

## Phase 11 - Testing

- [ ] Add categorization unit tests
- [ ] Add money formatting unit tests
- [ ] Add budget calculation unit tests
- [ ] Add recurring schedule unit tests
- [ ] Add CSV escaping unit tests
- [x] Add Room DAO tests
- [ ] Add dashboard ViewModel tests
- [ ] Add add-expense UI test

## Phase 12 - Portfolio Polish

- [ ] Add README screenshots
- [ ] Add architecture diagram
- [ ] Add sample data generator
- [ ] Add demo GIF or short screen recording
- [ ] Add recruiter-facing feature summary
- [ ] Add known limitations section based on actual implementation
