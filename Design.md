# Design

This document defines the UI and UX direction for Pulse based on the provided dark fintech mockup. The app should feel like a premium Nepal-focused finance tracker: fast, calm, visual, and built for daily use.

## Visual Direction

Pulse uses a dark, high-contrast fintech interface with rounded cards, vivid chart colors, compact data presentation, and Android-style navigation.

The reference image shows three primary patterns:

- A black app canvas with dark gray surfaces
- Large rounded panels for financial summaries
- Bright multi-color data visualization
- Bottom navigation in a floating rounded container
- Clear white typography with muted gray secondary text
- Touch-friendly rows and controls

Pulse should preserve that feeling while adapting the content from generic banking to Nepal-specific expense tracking.

## Product Personality

Pulse should feel:

- Trustworthy
- Local to Nepal
- Practical
- Premium but not flashy
- Data-rich without feeling crowded

It should not feel:

- Like a generic to-do app
- Like a crypto wallet
- Like a bank card management app
- Like a marketing landing page

## Color System

## App Colors

| Token | Color | Usage |
| --- | --- | --- |
| `background` | `#000000` | Main app background |
| `surface` | `#171717` | Cards and rows |
| `surfaceHigh` | `#222222` | Raised panels, bottom nav |
| `surfacePressed` | `#2E2E2E` | Pressed states |
| `textPrimary` | `#FFFFFF` | Main labels and values |
| `textSecondary` | `#B8B8B8` | Supporting labels |
| `textMuted` | `#7A7A7A` | Metadata |
| `primary` | `#2F80FF` | Active navigation, links, actions |
| `success` | `#35C76B` | Budget progress, positive indicators |
| `danger` | `#F25F7A` | Overspending, negative alerts |
| `warning` | `#FFB020` | Warnings and dining chart color |

## Category Colors

| Category | Color |
| --- | --- |
| Food & Dining | `#35C76B` |
| Groceries | `#2ECC71` |
| Transport | `#2F80FF` |
| Shopping | `#E94F86` |
| Utilities | `#FFB020` |
| Internet & TV | `#7C5CFF` |
| Wallet & Transfers | `#2DD4BF` |
| Mobile Recharge | `#38BDF8` |
| Education | `#A78BFA` |
| Health | `#F87171` |
| Fuel | `#F59E0B` |
| Rent & Housing | `#94A3B8` |
| Other | `#9CA3AF` |

## Typography

Use Material 3 typography as the base, tuned for dense financial UI.

Recommended scale:

| Role | Size | Weight | Usage |
| --- | --- | --- | --- |
| Screen title | 20sp | 600 | Analytics, Settings |
| Section title | 20sp | 600 | Budget, Categories |
| Large amount | 34sp | 500 | Monthly spend |
| Chart amount | 28sp | 500 | Donut chart center |
| Row title | 16sp | 500 | Transaction merchant |
| Body | 14sp | 400 | Row metadata |
| Caption | 12sp | 400 | Dates, percentages |
| Nav label | 11sp | 400 | Bottom navigation |

Rules:

- Use tabular numerals for money values if available.
- Keep letter spacing at `0`.
- Avoid oversized headings inside cards.
- Amounts should be easy to scan at a glance.

## Layout System

Use an 8dp spacing grid.

Recommended spacing:

- Screen horizontal padding: 24dp
- Card padding: 16dp to 20dp
- Section gap: 24dp
- Row gap: 10dp to 12dp
- Bottom nav height: 72dp
- Bottom safe area padding: device-dependent

## Corner Radius

| Element | Radius |
| --- | --- |
| Summary cards | 22dp |
| Category cards | 18dp |
| Transaction rows | 16dp |
| Bottom navigation | 22dp |
| Icon circles | 18dp to full circle |
| Segmented control | 22dp |

## Screen: Dashboard

The dashboard is the main landing screen.

Content order:

1. Header
2. Monthly spending card
3. Budget card
4. Category cards
5. Quick add row
6. Bottom navigation

## Header

The header should include:

- Profile avatar
- Greeting: `Welcome,`
- User name
- Search icon
- Notification icon with unread dot

For Nepal localization, example user names may use common Nepal names in sample data:

- Aayush Shrestha
- Srijana Thapa
- Nisha Karki
- Beatrice Cox can remain in mockups if matching the reference image

## Monthly Spending Card

Replace banking language like "balance" with expense-tracker language.

Use:

- Label: `This month`
- Amount: `रू 2,418.50`
- Action: circular plus icon
- Action label: `Add expense`

The plus button should be visually prominent and reachable with one thumb tap.

## Budget Card

The budget card should show:

- Title: `Budget`
- Remaining amount: `रू 1,081.50 left`
- Horizontal progress bar
- Current spend and total budget: `रू 2,418.50 of रू 3,500.00`
- Percentage: `69%`

States:

- Under 70%: green progress
- 70% to 90%: amber progress
- Over 90%: red progress
- Over budget: red amount and message `Over budget`

## Category Cards

Show three top categories on the dashboard.

Each card includes:

- Icon
- Category name
- Amount
- Percentage of monthly spend

Example Nepal-localized cards:

- Food: `रू 682.40`
- Transport: `रू 375.20`
- Utilities: `रू 575.30`

The reference image shows gradient-like category cards. In implementation, prefer subtle solid dark cards with colored icon circles and optional low-opacity color overlay.

## Quick Add

Quick add should help users enter common Nepal expenses quickly.

Recommended shortcuts:

- Other
- Food
- Pathao
- Daraz
- Fuel
- NTC/Ncell
- eSewa/Khalti

Each shortcut:

- Circular icon button
- Short label underneath
- Tap opens Add Expense with prefilled category or merchant

## Screen: Analytics

Analytics should focus on spending breakdown.

Content order:

1. Top app bar
2. Segmented control
3. Donut chart
4. Legend with amounts and percentages
5. Recent transactions

## Top App Bar

Include:

- Back arrow
- Center title: `Analytics`
- Calendar icon

Calendar icon opens period picker:

- This week
- This month
- Last month
- Custom range

## Segmented Control

Segments:

- `Spending`
- `Budget`

Selected segment:

- White pill background
- Dark text

Inactive segment:

- Dark gray background
- Muted text

## Donut Chart

The donut chart is a key visual element.

Requirements:

- Thick ring
- Rounded segment ends if feasible
- Multi-colored categories
- Center amount
- Center caption: `Total spend`
- Smooth animated transition when filters change

Example:

```text
रू 1,738.00
Total spend
```

## Legend

Legend items should include:

- Category color dot
- Category name
- Amount
- Percentage

Example:

```text
Groceries  रू 682.40  39%
Transport  रू 375.20  22%
Dining     रू 680.40  39%
```

## Recent Transactions

Rows should include:

- Circular merchant/category icon
- Merchant/title
- Category
- Amount
- Date label

Example Nepal-localized rows:

- Bhat-Bhateni, Groceries, `-रू 2,450`, Today
- Pathao, Transport, `-रू 280`, Today
- Foodmandu, Food & Dining, `-रू 890`, Yesterday
- WorldLink, Internet & TV, `-रू 1,500`, May 15

## Screen: Settings

Settings should feel sparse and controlled.

Content order:

1. Top app bar
2. Profile block
3. Settings rows
4. Bottom navigation

## Profile Block

Include:

- Avatar
- Name
- Email

Avoid large decorative profile cards. The reference image keeps the profile centered and simple.

## Settings Rows

Rows should be rounded and separated by 8dp gaps.

Recommended rows:

- Monthly budget
- CSV export
- Currency
- Notifications
- Recurring expenses
- Categories
- Help center

For Nepal:

- Currency default should show `NPR (रू)`
- Monthly budget examples should use NPR
- CSV export should preserve Nepali Unicode text

## Bottom Navigation

Use a floating rounded bottom nav matching the reference.

Items:

- Home
- Analytics
- Transactions
- Settings

Use clear icons from Lucide or Material Icons:

- Home
- Bar chart
- Receipt/list
- Settings

Behavior:

- Active item uses `primary` blue
- Inactive items use white or muted gray
- Labels stay visible
- Navigation bar remains above system gesture area

The reference uses `Transfer`, but Pulse should use `Transactions` because this is not a banking transfer app.

## Screen: Add Expense

Add Expense is not shown in the reference image but should inherit the same visual language.

Content order:

1. Top app bar with close/back icon and title `Add Expense`
2. Large amount input
3. Merchant/title field
4. Category selector
5. Date selector
6. Payment method selector
7. Recurring toggle
8. Note field
9. Save button

## Amount Input

Use a large money input:

```text
रू 0.00
```

Rules:

- Default currency is NPR.
- Numeric keypad should open automatically.
- The amount should remain readable even with large values.

## Category Selector

Show category chips or a grid.

Category selection should support auto-suggestion:

```text
Suggested: Transport
Reason: matched "Pathao"
```

Do not overexplain this in the UI. A small suggested label is enough.

## Payment Methods

Recommended options:

- Cash
- eSewa
- Khalti
- Fonepay
- Bank
- Card

## Recurring Toggle

When enabled, reveal:

- Frequency
- Start date
- Next due date

Common Nepal recurring examples:

- Rent
- NEA electricity
- Khanepani water
- WorldLink/Vianet internet
- NTC/Ncell mobile pack
- School fees

## Empty States

Empty states should be short and action-oriented.

Examples:

- Dashboard: `No expenses this month`
- Transactions: `No transactions found`
- Analytics: `Add expenses to see insights`

Avoid large illustrations. The dark fintech style works better with compact empty states and a primary action.

## Motion

Use subtle motion only:

- Chart segment animation on period/category change
- Progress bar fill animation
- Press/ripple states
- Bottom nav active item transition

Avoid excessive bouncing or decorative animation.

## Accessibility

Requirements:

- Minimum touch target: 48dp
- Text contrast must pass WCAG AA
- Charts must have text summaries
- Category colors must not be the only signal
- Currency values should have accessible descriptions
- Toggles need clear labels

## Nepal Localization Notes

MVP language can be English, but the app should support Nepal usage patterns.

Required:

- NPR as default currency
- Nepal merchant examples
- Nepal payment method examples
- Unicode-safe text input and CSV export

Post-MVP:

- Nepali language UI
- Bikram Sambat date display
- Fiscal year reports
- Bank SMS parsing if permissions and privacy concerns are handled carefully

