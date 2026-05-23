# Progress

## Current Status

Status: Phase 1 foundation implemented

The project now has a buildable Android app foundation for a Nepal-focused version of Pulse. The app uses Kotlin, Jetpack Compose, Material 3, Navigation Compose, and a dark Pulse theme with static Nepal-specific preview data.

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

## In Progress

- Phase 2 local database planning

## Not Started

- Room database implementation
- Expense CRUD
- Real dashboard state from persisted expenses
- Production analytics charts
- Recurring expenses
- CSV export
- Automated tests

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

Implement the local database and first domain rules:

1. Room database with expenses and categories
2. Seed default Nepal categories and keyword aliases
3. Nepal keyword categorization use case
4. Unit tests for categorization and money formatting
5. Connect Add Expense and Dashboard to real data
