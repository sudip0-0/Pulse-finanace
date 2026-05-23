# Architecture

Pulse uses MVVM with Clean Architecture boundaries. The goal is to keep UI code declarative, business logic testable, and persistence isolated behind repositories.

## High-Level Structure

```text
app/
  presentation/
    dashboard/
    analytics/
    expense/
    transactions/
    recurring/
    settings/
    common/
  domain/
    model/
    repository/
    usecase/
    categorization/
  data/
    local/
      dao/
      entity/
      database/
    mapper/
    repository/
  di/
```

## Layer Responsibilities

## Presentation Layer

The presentation layer contains Jetpack Compose screens, ViewModels, UI state models, and UI events.

Responsibilities:

- Render immutable UI state
- Send user events to ViewModels
- Collect `StateFlow` using lifecycle-aware APIs
- Display loading, empty, and error states
- Avoid direct Room or repository access

Example state shape:

```kotlin
data class DashboardUiState(
    val monthLabel: String = "",
    val monthlySpend: Money = Money.zero(),
    val monthlyBudget: Money? = null,
    val budgetPercent: Float = 0f,
    val categoryBreakdown: List<CategorySpendUiModel> = emptyList(),
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val quickAddItems: List<QuickAddUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
```

## Domain Layer

The domain layer contains app rules independent of Android APIs.

Responsibilities:

- Expense validation
- Nepal-specific auto-categorization
- Budget calculations
- Chart aggregation
- Recurring expense scheduling
- CSV row preparation
- Repository contracts

Recommended use cases:

- `AddExpenseUseCase`
- `UpdateExpenseUseCase`
- `DeleteExpenseUseCase`
- `ObserveDashboardUseCase`
- `ObserveTransactionsUseCase`
- `CategorizeExpenseUseCase`
- `CreateRecurringExpenseUseCase`
- `GenerateDueRecurringExpensesUseCase`
- `ExportTransactionsCsvUseCase`
- `CalculateBudgetProgressUseCase`

## Data Layer

The data layer owns Room entities, DAOs, database configuration, mappers, and repository implementations.

Responsibilities:

- Persist expenses, categories, budgets, and recurring rules
- Expose reactive `Flow` queries
- Map database entities to domain models
- Run database work on IO dispatchers
- Keep SQL-specific behavior out of the UI

## Data Flow

```text
Compose Screen
  -> ViewModel event
  -> UseCase
  -> Repository interface
  -> Repository implementation
  -> Room DAO
  -> SQLite
  -> Flow emits changes
  -> ViewModel StateFlow updates
  -> Compose recomposes
```

## Room Database Model

## `expenses`

Stores individual expense records.

```text
id: Long
title: String
merchant: String?
amountMinor: Long
currencyCode: String
categoryId: Long
paymentMethod: String?
expenseDate: LocalDate
note: String?
isRecurringGenerated: Boolean
recurringRuleId: Long?
createdAt: Instant
updatedAt: Instant
```

Use `amountMinor` to store paisa as an integer. For example, `रू 150.50` should be stored as `15050`.

## `categories`

Stores default and user-created categories.

```text
id: Long
name: String
iconKey: String
colorHex: String
sortOrder: Int
isDefault: Boolean
isArchived: Boolean
```

## `category_keywords`

Stores keywords used by the auto-categorization engine.

```text
id: Long
categoryId: Long
keyword: String
matchType: String
weight: Int
locale: String
```

Example values:

```text
pathao -> Transport
tootle -> Transport
foodmandu -> Food & Dining
bhojdeals -> Food & Dining
bhatbhateni -> Groceries
bhat-bhateni -> Groceries
daraz -> Shopping
esewa -> Wallet & Transfers
khalti -> Wallet & Transfers
ntc -> Mobile Recharge
ncell -> Mobile Recharge
nea -> Utilities
khanepani -> Utilities
worldlink -> Internet & TV
vianet -> Internet & TV
cg net -> Internet & TV
dishhome -> Internet & TV
```

## `recurring_rules`

Stores repeat rules for predictable expenses.

```text
id: Long
title: String
merchant: String?
amountMinor: Long
currencyCode: String
categoryId: Long
frequency: String
interval: Int
startDate: LocalDate
nextDueDate: LocalDate
endDate: LocalDate?
isActive: Boolean
createdAt: Instant
updatedAt: Instant
```

Supported MVP frequencies:

- Weekly
- Monthly
- Yearly

## `budgets`

Stores monthly budgets.

```text
id: Long
month: YearMonth
amountMinor: Long
currencyCode: String
createdAt: Instant
updatedAt: Instant
```

## Auto-Categorization Strategy

The MVP should use deterministic local rules rather than machine learning. This keeps the feature explainable, testable, and offline-first.

Categorization steps:

1. Normalize the transaction title, merchant, and note.
2. Convert text to lowercase.
3. Remove punctuation and duplicate whitespace.
4. Apply exact merchant matches first.
5. Apply weighted keyword matches.
6. Resolve ties by category priority.
7. Fall back to the user's previous choice for the same merchant.
8. Fall back to `Other`.

Example priority:

```text
Exact merchant > Strong keyword > Previous merchant category > Weak keyword > Other
```

## Nepal Market Keyword Groups

## Food & Dining

- foodmandu
- bhojdeals
- restaurant
- cafe
- momo
- khaja
- lunch
- dinner
- tea
- coffee
- bakery
- pizza
- burger

## Groceries

- bhatbhateni
- bhat-bhateni
- big mart
- salesberry
- grocery
- tarkari
- vegetable
- fruit
- kirana
- mart

## Transport

- pathao
- tootle
- indrive
- taxi
- bus
- micro
- tempo
- ride
- fare
- parking

## Shopping

- daraz
- sastodeal
- shopping
- clothes
- shoes
- electronics
- gift
- mall

## Wallet & Transfers

- esewa
- khalti
- ime pay
- connect ips
- fonepay
- bank transfer
- qr payment
- wallet

## Mobile Recharge

- ntc
- ncell
- smart cell
- recharge
- data pack
- voice pack
- topup

## Utilities

- nea
- electricity
- bijuli
- khanepani
- water bill
- garbage

## Internet & TV

- worldlink
- vianet
- classic tech
- dishhome
- cg net
- subisu
- internet
- wifi
- tv

## Education

- school fee
- college fee
- tuition
- books
- stationery
- exam fee

## Rent & Housing

- rent
- room rent
- flat rent
- house rent
- maintenance

## Health

- hospital
- clinic
- pharmacy
- medicine
- dental
- lab test

## Fuel

- petrol
- diesel
- fuel
- nepal oil
- charging

## Reactive State

Room DAOs should expose `Flow` where the UI needs live updates.

Examples:

```kotlin
@Query("SELECT * FROM expenses WHERE expenseDate BETWEEN :start AND :end ORDER BY expenseDate DESC")
fun observeExpensesBetween(start: LocalDate, end: LocalDate): Flow<List<ExpenseEntity>>
```

ViewModels should convert domain flows into `StateFlow`:

```kotlin
val uiState: StateFlow<DashboardUiState> =
    observeDashboardUseCase()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
```

## Date and Currency Handling

- Default currency: `NPR`
- Display format: `रू 2,418.50` or `NPR 2,418.50`
- Store money in minor units as `Long`
- Store dates as ISO values internally
- Consider Bikram Sambat calendar support as a post-MVP enhancement

## CSV Export

CSV columns:

```text
Date,Title,Merchant,Category,Amount,Currency,Payment Method,Note,Recurring
```

Export requirements:

- Use UTF-8
- Escape commas and quotes correctly
- Preserve Nepali merchant names if entered
- Use Android document creation APIs so the user chooses the save location

## Testing Strategy

Unit tests:

- Categorization rules
- Budget progress calculation
- Money formatting
- Recurring schedule calculation
- CSV escaping

Database tests:

- DAO insert/update/delete
- Date range queries
- Category aggregation queries

UI tests:

- Add expense flow
- Dashboard updates after insert
- Filter transactions by category
- Export action opens document flow

