# Project Structure

## Top-Level Layout

```
Pulse-finanace/
├── app/                    # Single application module
│   ├── build.gradle.kts
│   ├── schemas/            # Room schema exports (version history)
│   └── src/
│       ├── main/
│       └── androidTest/
├── build.gradle.kts        # Root build file (plugin versions)
├── settings.gradle.kts     # Module includes
├── Architecture.md         # Detailed architecture reference
├── Design.md               # UX/UI design reference
├── Agents.md               # AI agent coding guidelines
├── Tasks.md                # Current task tracking
├── Progress.md             # Milestone log
└── KnownIssues.md          # Tracked limitations/defects
```

## Source Package: `com.pulsefinance`

Follows MVVM + Clean Architecture with three layers:

```
com.pulsefinance/
├── MainActivity.kt
├── data/                       # Data layer
│   ├── local/
│   │   ├── dao/                # Room DAOs (ExpenseDao, CategoryDao, BudgetDao, etc.)
│   │   ├── database/           # PulseDatabase, migrations, seed data, type converters
│   │   └── entity/             # Room entities (ExpenseEntity, CategoryEntity, etc.)
│   ├── mapper/                 # Entity ↔ domain model mappers
│   └── repository/             # Repository implementations
├── di/                         # Dependency injection (Hilt modules when added)
├── domain/                     # Domain layer (pure Kotlin, no Android deps)
│   ├── categorization/         # Nepal-specific auto-categorization engine
│   ├── model/                  # Domain models (Expense, Money, Category, Budget, etc.)
│   ├── repository/             # Repository interfaces/contracts
│   └── usecase/                # Business logic use cases
└── presentation/               # Presentation layer
    ├── analytics/              # Analytics/charts screen
    ├── common/
    │   ├── components/         # Shared composables
    │   └── theme/              # Material 3 theme (dark fintech)
    ├── dashboard/              # Home dashboard screen
    ├── expense/                # Add/edit expense screen
    ├── navigation/             # Nav graph and route definitions
    ├── preview/                # Compose preview data
    ├── recurring/              # Recurring expenses screen
    ├── settings/               # Settings screen
    └── transactions/           # Transaction list screen
```

## Architecture Rules

- Screens never call DAOs directly — always go through ViewModel → UseCase → Repository
- ViewModels expose `StateFlow<UiState>` (immutable)
- Domain layer has no Android framework dependencies
- Repositories hide Room implementation details
- Room DAOs expose `Flow` for reactive queries
