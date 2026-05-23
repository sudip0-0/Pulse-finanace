# Product: Pulse

Pulse is a Nepal-focused Android personal finance tracker. It is offline-first, privacy-respecting, and designed for the Nepali market.

## Core Functionality

- Manual expense tracking with NPR as default currency
- Auto-categorization using Nepal-specific merchant/keyword rules (Pathao, eSewa, Bhat-Bhateni, etc.)
- Monthly budgets with progress tracking
- Recurring expense scheduling (weekly, monthly, yearly)
- Category-based analytics and spending breakdown
- CSV export of transactions
- Dashboard with monthly spend summary, category breakdown, and recent transactions

## Product Constraints

- Offline-first: no remote backend for MVP
- Privacy-first: no SMS reading, notification scraping, or bank integration without explicit opt-in
- Nepal market: sample data, categories, and merchants should reflect Nepali context
- Expense tracker language, not banking language (use "Transactions" not "Transfer")
- Money stored as integer minor units (paisa); displayed as `रू 2,418.50`

## UX Direction

- Dark fintech UI with rounded dark cards
- Multi-color charts (Compose Canvas preferred over heavy libraries)
- Floating bottom navigation
- NPR-first money display
