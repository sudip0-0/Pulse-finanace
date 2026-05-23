# Pulse - Smart Personal Finance Tracker for Nepal

Pulse is an offline-first Android personal finance tracker designed for Nepal-based users. It tracks daily expenses, auto-categorizes transactions using Nepal-specific merchant and keyword rules, visualizes spending patterns, supports recurring expenses, and exports transaction data to CSV.

The project is intended as an intermediate portfolio app that demonstrates modern Android development with Jetpack Compose, Room, MVVM, Clean Architecture, Kotlin Coroutines, and reactive `StateFlow` UI state.

## Portfolio Pitch

Pulse helps users understand where their money goes across common Nepal spending patterns: groceries, Pathao rides, Foodmandu orders, Daraz shopping, eSewa/Khalti wallet payments, NTC/Ncell recharges, electricity bills, rent, school fees, and subscriptions.

The app is not a generic budgeting clone. It is localized around:

- Nepalese Rupees (`NPR`, `रू`)
- Nepal market merchants and payment behavior
- Local recurring bills such as NEA electricity, Khanepani water, ISP bills, mobile top-ups, rent, and school fees
- CSV export for spreadsheet-friendly personal accounting
- Dark premium fintech UI inspired by modern mobile banking apps

## Core Features

- Add, edit, and delete expenses
- Auto-categorize expenses using Nepal-specific merchant and keyword rules
- View monthly spending summary
- Track budget usage and remaining amount
- View category breakdown with donut charts
- View weekly/monthly spending with bar charts
- Search and filter transactions by date, category, amount, and merchant
- Create recurring expenses for monthly bills
- Export transactions to CSV
- Manage categories and category colors
- Offline-first local storage with Room

## Suggested Tech Stack

- Language: Kotlin
- UI: Jetpack Compose with Material 3
- Architecture: MVVM + Clean Architecture
- Local database: Room
- Reactive streams: Kotlin Flow and StateFlow
- Async work: Kotlin Coroutines
- Dependency injection: Hilt
- Navigation: Navigation Compose
- Charts: Compose Canvas custom charts
- Testing: JUnit, Turbine, Room in-memory tests, Compose UI tests

## Main Screens

- Dashboard
- Add Expense
- Transactions
- Analytics
- Recurring Expenses
- Settings

## Current Implementation Status

Phase 1 project foundation, Phase 2 local database, and Phase 3 domain layer are implemented:

- Android `:app` module
- Kotlin and Jetpack Compose
- Material 3 dark Pulse theme
- Navigation routes for Dashboard, Analytics, Transactions, Add Expense, Recurring, and Settings
- Clean Architecture package boundaries
- Shared Compose components
- Static Nepal-specific preview data using NPR examples
- Room database schema for expenses, categories, category keywords, recurring rules, and budgets
- Deterministic Nepal category and keyword seed data
- Type converters for local dates, instants, and year-month values
- DAO coverage for CRUD, date ranges, category aggregation, recurring rules, budgets, and keyword lookup
- Pure Kotlin domain models using Long minor units for money
- Repository contracts for expenses, categories, category keywords, budgets, and recurring rules
- Deterministic Nepal-focused categorization use case
- Use cases for expense writes, dashboard and transaction observation, budget progress, recurring generation, and CSV export
- Unit tests for money formatting, categorization, budget progress, recurring generation, CSV escaping, and validation

Repository implementations, Hilt dependency injection, and UI integration with persisted Room data are implemented. Adding an expense now writes through the domain use case and repository into Room, and the dashboard observes Room-backed flows so monthly totals, category spending, and recent transactions refresh reactively.

## Nepal-Specific Categories

Default categories should include:

- Food & Dining
- Groceries
- Transport
- Shopping
- Wallet & Transfers
- Mobile Recharge
- Utilities
- Rent & Housing
- Internet & TV
- Education
- Health
- Entertainment
- Fuel
- Travel
- Savings
- Other

## Example Auto-Categorization

| Input text | Suggested category |
| --- | --- |
| Pathao ride to Baneshwor | Transport |
| Tootle trip | Transport |
| Foodmandu dinner | Food & Dining |
| Bhat-Bhateni groceries | Groceries |
| Daraz order | Shopping |
| NTC recharge | Mobile Recharge |
| Ncell data pack | Mobile Recharge |
| NEA electricity bill | Utilities |
| Khanepani bill | Utilities |
| WorldLink internet | Internet & TV |
| Vianet payment | Internet & TV |
| eSewa transfer | Wallet & Transfers |
| Khalti payment | Wallet & Transfers |
| Petrol at Nepal Oil | Fuel |
| School fee | Education |

## Success Criteria

The MVP is complete when a user can:

1. Add an expense in NPR.
2. See it instantly reflected in dashboard totals and charts.
3. Have the app suggest a category from Nepal-specific keywords.
4. Create a recurring expense.
5. Search and filter transaction history.
6. Export transactions as CSV.

## Repository Documentation

- [Architecture.md](Architecture.md) - app architecture, layers, state flow, database design
- [Design.md](Design.md) - UI/UX system based on the provided dark finance mockup
- [Tasks.md](Tasks.md) - implementation backlog
- [Progress.md](Progress.md) - current project status
- [KnownIssues.md](KnownIssues.md) - risks, limitations, and unresolved issues
- [Agents.md](Agents.md) - coding-agent instructions for future implementation work
