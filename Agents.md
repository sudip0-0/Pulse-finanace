# Agents

This file gives implementation instructions for coding agents working on Pulse.

## Project Intent

Pulse is a Nepal-focused Android personal finance tracker. Preserve that product direction during implementation. Do not drift toward a generic banking app, crypto wallet, or to-do list.

## Primary Stack

Use:

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Kotlin Coroutines
- Flow and StateFlow
- MVVM with Clean Architecture
- Hilt if dependency injection is added

Avoid introducing:

- Remote backend services for the MVP
- Firebase unless explicitly requested
- Cross-platform frameworks
- Heavy charting libraries unless custom Compose Canvas proves too expensive

## UX Direction

Follow `Design.md`.

Important design constraints:

- Dark fintech UI
- Rounded dark cards
- Multi-color charts
- Floating bottom navigation
- NPR-first money display
- Nepal-specific sample data
- Expense tracker language, not banking language

Use `Transactions`, not `Transfer`, in bottom navigation.

## Architecture Direction

Follow `Architecture.md`.

Rules:

- Compose screens should not call DAOs directly.
- ViewModels expose immutable `StateFlow` UI state.
- Domain use cases own business logic.
- Repositories hide Room implementation details.
- Money should be stored as integer minor units.
- Auto-categorization should be deterministic and locally testable.

## Nepal Auto-Categorization

The app must include Nepal-market categorization rules. At minimum, support:

- Pathao, Tootle, inDrive -> Transport
- Foodmandu, Bhojdeals, momo, khaja -> Food & Dining
- Bhat-Bhateni, Big Mart, SalesBerry, kirana -> Groceries
- Daraz, SastoDeal -> Shopping
- eSewa, Khalti, IME Pay, ConnectIPS, Fonepay -> Wallet & Transfers
- NTC, Ncell, recharge, data pack -> Mobile Recharge
- NEA, electricity, Khanepani -> Utilities
- WorldLink, Vianet, Classic Tech, DishHome, CG Net, Subisu -> Internet & TV
- Rent, room rent, flat rent -> Rent & Housing
- School fee, college fee, tuition -> Education

When adding new categories or sample data, prefer Nepal-relevant examples.

## Documentation Discipline

When implementation starts:

- Update `Progress.md` after meaningful milestones.
- Update `Tasks.md` as work is completed or reprioritized.
- Add limitations and defects to `KnownIssues.md`.
- Keep README aligned with actual implemented features.

## Testing Expectations

Prioritize tests for:

- Categorization rules
- Money formatting
- Recurring expense generation
- Budget calculations
- CSV escaping
- DAO date range queries

For UI changes, test the primary add-expense flow and dashboard refresh behavior.

## Code Style

- Prefer small composables with clear responsibilities.
- Keep UI state models explicit.
- Avoid stringly typed categories in business logic once domain models exist.
- Use preview data that reflects Nepal market behavior.
- Keep comments rare and useful.

## Privacy and Safety

Pulse should be offline-first. Do not add SMS reading, notification scraping, bank login, or wallet integration without explicit user approval and a privacy review.

If bank SMS parsing is added later, it must be opt-in, transparent, and locally processed by default.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.
