# Known Issues

This file tracks known limitations, risks, and unresolved decisions.

## Product Limitations

## No Bank or Wallet Sync

The MVP is offline-first and does not connect to banks, eSewa, Khalti, Fonepay, or other wallet providers.

Impact:

- Users must enter expenses manually.
- Auto-categorization works from entered text, not real transaction feeds.

Reason:

- Local-first manual tracking is safer and more realistic for an intermediate portfolio project.

## No SMS Parsing

The MVP should not read transaction SMS messages.

Impact:

- The app cannot automatically import bank or wallet messages.

Reason:

- SMS access introduces privacy, permission, reliability, and Play Store policy concerns.

## Bikram Sambat Not Yet Included

The MVP uses standard date handling internally and may display Gregorian dates first.

Impact:

- Some Nepal users may expect Bikram Sambat date support.

Potential resolution:

- Add optional BS display mode after the core app is stable.

## Auto-Categorization Is Rule-Based

The MVP uses deterministic keyword and merchant rules.

Impact:

- It may misclassify ambiguous transactions.
- It may not learn complex behavior automatically.

Potential resolution:

- Store user corrections per merchant.
- Add confidence scoring.
- Add user-editable keywords.

## Merchant Names May Vary

Nepal merchant names can be entered inconsistently.

Examples:

- `Bhatbhateni`
- `Bhat-Bhateni`
- `BBSM`
- `Bhat Bhateni Super Market`

Impact:

- Categorization may miss some variants.

Potential resolution:

- Add normalization aliases.
- Add fuzzy matching carefully.
- Let users assign a category and remember it.

## CSV Export Only

The MVP supports CSV export but not import.

Impact:

- Users cannot restore or migrate data from CSV yet.

Current state:

- CSV export is implemented via Android CreateDocument API (SAF).
- No storage permissions required.
- UTF-8 with BOM for Excel compatibility.
- Nepali Unicode text is preserved.
- Export covers current month's expenses.

Potential resolution:

- Add CSV import with duplicate detection.
- Add date range selection for export.

## Single Currency First

The app defaults to NPR.

Impact:

- Users with foreign expenses may need manual notes.

Potential resolution:

- Add per-expense currency later.
- Add exchange-rate support only if there is a clear need.

## Technical Risks

## Domain Repositories Are Now Wired Through Hilt

All phases through Phase 10 are connected through Hilt DI modules and reactive ViewModels.

Impact:

- Dashboard, transactions, analytics, recurring, and settings screens all read real persisted data.
- Recurring expense generation runs on app start via DashboardViewModel.
- CSV export and budget editing are functional.

Remaining:

- Category management is intentionally minimal for MVP: custom categories can be added, edited, and archived, while default seeded categories are protected.

## Chart Rendering Complexity

Custom Compose Canvas charts require careful implementation.

Risks:

- Label overlap
- Poor accessibility
- Animation bugs
- Incorrect touch handling

Potential resolution:

- Keep MVP charts simple.
- Add text summaries for accessibility.
- Add screenshot-based visual QA if possible.

## Room Migration Risk

Database schema will evolve as features are added.

Risks:

- Broken migrations
- Lost local data during upgrades

Potential resolution:

- Define migrations early.
- Add migration tests.
- Avoid destructive schema changes after sample data exists.

Current state:

- Schema version `1` is exported.
- Phase 2 schema hardening was folded into version `1` before persisted user data exists.
- No historical migrations exist yet because this is the first Room schema.
- Future schema changes must add explicit migrations before release builds depend on local user data.

## Instrumented Database Tests Need A Device

Room DAO coverage has been added under `androidTest`.

Impact:

- The Android test APK compiles locally.
- DAO tests cannot be executed in this environment without an attached emulator or device.
- The add-expense UI test (Compose test) has been added but requires an instrumented environment to execute.

Potential resolution:

- Run `.\gradlew.bat connectedDebugAndroidTest` with a device or emulator.
- Add Robolectric later if JVM-only DAO tests become a priority.

Current state:

- 156 JVM unit tests pass via `.\gradlew.bat :app:testDebugUnitTest`.
- Instrumented tests compile via `.\gradlew.bat :app:assembleDebugAndroidTest`.
- Execution requires a connected device or emulator.

## Time Zone and Date Boundaries

Expense grouping by day/month must respect the user's local timezone.

Impact:

- Monthly totals could be wrong around midnight or if timezone changes.

Potential resolution:

- Use local dates for expense dates.
- Use instants only for creation/update timestamps.

## Recurring Expense Duplication

Recurring expenses can accidentally generate duplicates.

Impact:

- Dashboard totals become incorrect.

Potential resolution:

- Store `recurringRuleId` on generated expenses.
- Check whether a rule already generated an expense for a due period.
- Add unit tests for missed and repeated due dates.

Current state:

- Domain generation checks for an existing generated expense before creating one.
- The DAO has `hasGeneratedExpenseForRule(ruleId, date)` for atomic duplicate detection.
- The DashboardViewModel triggers generation on app start.
- Unit tests cover missed dates, already-generated dates, inactive rules, and end-date boundaries.
- Month-end behavior is handled by the domain model's `nextDateAfter()` logic (preserves last-day-of-month semantics).
- Monthly rules anchored to day 29/30 recover after short months (e.g., Jan 30 → Feb 28 → Mar 30).
- Concurrent generation is guarded by an AtomicBoolean to prevent race-condition duplicates.
- Resuming a paused rule advances `nextDueDate` past today to avoid surprising back-dated expense generation.

## UI Risks

## Runtime UI Needs Device Verification

The app compiles and the ViewModel/domain/database paths are covered by JVM tests and Android test APK compilation, but the interactive Compose UI has not been launched on a device or emulator in this environment.

Impact:

- Screen rendering is verified by Compose compilation only.
- The add-expense end-to-end UI flow still needs an instrumented run.
- Layout issues may still appear on real screen sizes.

Potential resolution:

- Run the app on an emulator or device.
- Run `.\gradlew.bat connectedDebugAndroidTest` once a device or emulator is attached.
- Add screenshots after the first device pass.

## Dark Theme Contrast

The design is dark and visually dense.

Risk:

- Muted text may become too low contrast.

Potential resolution:

- Validate contrast for secondary labels.
- Keep important financial values white.

## Small Screen Crowding

Charts, legends, and transaction rows can crowd small Android devices.

Potential resolution:

- Use vertical scrolling.
- Keep dashboard cards compact.
- Avoid placing too many metrics in one row.
