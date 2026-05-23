# Tech Stack

## Language & Platform

- Kotlin (JVM 17)
- Android (minSdk 26, targetSdk 36, compileSdk 36)
- Single module: `:app`

## Build System

- Gradle 8.14.3 with Kotlin DSL (`build.gradle.kts`)
- AGP 8.13.1
- Kotlin 2.2.20

## Core Libraries

| Purpose | Library |
|---------|---------|
| UI | Jetpack Compose (BOM 2025.10.01), Material 3 |
| Navigation | Navigation Compose 2.9.5 |
| Database | Room 2.8.4 (with kapt annotation processing) |
| Lifecycle | Lifecycle Runtime Compose 2.9.4 |
| DI | Hilt (when added) |
| Icons | Material Icons Extended |

## Key Plugins

- `com.android.application`
- `org.jetbrains.kotlin.android`
- `org.jetbrains.kotlin.plugin.compose`
- `org.jetbrains.kotlin.kapt`
- `androidx.room`

## Common Commands

```shell
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Generate Room schema (auto-exported to app/schemas/)
./gradlew kaptDebugKotlin
```

## Testing

- Unit tests: JUnit 4 (`testImplementation`)
- Instrumented tests: AndroidX Test + Room Testing (`androidTestImplementation`)
- Priority test targets: categorization rules, money formatting, recurring expense generation, budget calculations, CSV escaping, DAO date range queries

## Avoid Introducing

- Remote backend services (MVP is offline-only)
- Firebase (unless explicitly requested)
- Cross-platform frameworks
- Heavy charting libraries (prefer Compose Canvas)
