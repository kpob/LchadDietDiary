# CLAUDE.md — LchadDietDiary

Guidance for AI assistants working in this repository.

---

## Project Overview

**LchadDietDiary** is an Android application (Kotlin) for tracking daily meals for a child with LCHAD (Long-chain 3-hydroxy acyl-CoA dehydrogenase deficiency) — a rare metabolic disorder requiring a strict low-LCT/MCT diet. The app records meals and ingredients, syncs via Firebase Realtime Database, and visualises nutritional data through pie charts.

The app was originally written in 2017 and was modernised in 2024 (Gradle 8.7, AGP 8.3.2, Kotlin 2.1.10, AndroidX, Firebase BoM 33.x, Realm 10.x).

---

## Repository Structure

```
LchadDietDiary/
├── app/                          # Main Android application module
│   ├── build.gradle
│   ├── google-services.json      # Firebase project config (tracked; treat as non-secret)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/credentials    # Firebase email/password (NOT tracked in git — add manually)
│       ├── java/pl/kpob/dietdiary/
│       │   ├── App.kt            # Application class — Realm init, shortcuts, StrictMode
│       │   ├── AppPrefs.kt       # SharedPreferences wrapper (token, isInitialized)
│       │   ├── MainActivity.kt   # Single Activity; Firebase sync, Magellan host
│       │   ├── OcrActivity.kt    # Camera/OCR capture (experimental)
│       │   ├── Prefrences.kt     # Base class for SharedPreferences wrappers
│       │   ├── utils.kt          # Top-level extension functions (Realm, Firebase, View, Screen)
│       │   ├── db/               # Realm data-transfer objects (DTOs) + IngredientCategory enum
│       │   ├── domain/           # Immutable domain models + MealType enum
│       │   ├── firebase/         # Firebase model classes + FirebaseSaver + listener DSL
│       │   ├── fcm/              # Firebase Cloud Messaging services
│       │   ├── mapper/           # Hand-written mappers (MealMapper, MealDetailsMapper)
│       │   ├── ocr/              # OCR detector (Java; Google Vision API)
│       │   ├── repo/             # Repository + Specification + Transaction interfaces/impls
│       │   ├── screens/          # Magellan Screen subclasses (one per UI screen)
│       │   │   └── utils/        # Screen-scoped helpers (interactors, TimePicker, Traits)
│       │   └── views/            # Custom Android View subclasses (one per Screen)
│       └── res/                  # Standard Android resources
├── mapper-annotation/            # Java annotation definitions (@AutoMapping, @MapAs, @Ignore)
├── mapper-proccesor/             # Kotlin annotation processor (KotlinPoet-based code gen)
├── gradle/wrapper/               # Gradle wrapper (gradle-8.7-all)
├── build.gradle                  # Root build script
├── settings.gradle               # Module includes: app, mapper-annotation, mapper-proccesor
├── gradle.properties             # JVM args, AndroidX flags, Kotlin code style
├── gradlew / gradlew.bat
└── TOOLS_UPDATE_PLAN.md          # Historical record of the 2024 tooling modernisation
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1.10 (+ Java for OCR/annotation source) |
| Build system | Gradle 8.7, Android Gradle Plugin 8.3.2 |
| Min SDK | 21 (Android 5.0) |
| Compile/Target SDK | 35 (Android 15) |
| Navigation | Magellan 1.0.0 (`com.wealthfront:magellan`) |
| Local database | Realm 10.18.0 |
| Remote backend | Firebase Realtime Database (BoM 33.8.0) |
| Auth | Firebase Authentication (email/password) |
| Push notifications | Firebase Cloud Messaging |
| Crash reporting | Firebase Crashlytics |
| Date/time | ThreeTenABP 1.4.7 (java.time backport) |
| Charts | MPAndroidChart v3.1.0 |
| View binding | ViewBinding (enabled in AGP; replaces synthetic imports) |
| Desugaring | `com.android.tools:desugar_jdk_libs:2.1.4` |
| JVM target | Java 11 |

---

## Architecture

### Pattern: Single Activity + Magellan Screens

`MainActivity` is the sole Activity. Navigation is managed by Magellan's `Navigator`. Each logical screen is a `RxScreen<V>` subclass paired with a custom `View` subclass:

```
Screen (business logic)  →  View (XML layout + user events)
MainScreen               →  MainView
AddMealScreen            →  AddMealView
AddIngredientScreen      →  AddIngredientView
IngredientsListScreen    →  IngredientsListView
PieChartScreen           →  PieChartView
TagCloudScreen           →  TagCloudView
```

Screens receive lifecycle callbacks from Magellan (`onSubscribe`, `onUnsubscribe`). Views delegate user interactions back to the Screen by holding a typed reference.

### Data Flow

```
Firebase Realtime DB  (source of truth)
        │
        │  ValueEventListener (MainActivity)
        ▼
    Realm DB  (local offline cache, auto-synced)
        │
        │  Repository + Specification query
        ▼
   Domain model  (immutable data classes)
        │
        │  Screen reads domain model
        ▼
      View  (displays data)
```

**Writes** go to Firebase first (via `FirebaseSaver`). The Firebase listener in `MainActivity` receives the change and writes it to Realm asynchronously. The Realm change listener on `MainActivity` then triggers a view refresh.

### Three-Layer Data Model

Every entity exists in three representations:

| Layer | Example | Purpose |
|---|---|---|
| `db/` — Realm DTO | `MealDTO`, `IngredientDTO`, `TagDTO` | Persisted locally; `RealmObject` subclasses |
| `firebase/` — Firebase model | `FbMeal`, `FbIngredient` | Serialised to/from Firebase; implements `FirebaseModel<T>` |
| `domain/` — Domain model | `Meal`, `MealDetails`, `Ingredient` | Immutable data classes consumed by Screens/Views |

Conversion path: Firebase JSON → `FbX.toRealm()` → `XDto` → `Mapper.map()` → domain object.

---

## Custom Annotation Processor (`mapper-annotation` + `mapper-proccesor`)

Realm DTO classes annotated with `@AutoMapping` trigger code generation at compile time (via `kapt`). The processor (`MappedProcessor`) generates:

- **Domain model** data class (e.g., `Ingredient` from `IngredientDTO`)
- **Firebase model** data class (e.g., `FbIngredient`)
- **Contract object** with table/field name constants (e.g., `IngredientContract.TABLE_NAME`)
- **Repository class** (e.g., `IngredientRepository`)
- **Mapper class** (e.g., `IngredientMapper`)

### Annotation Reference

| Annotation | Scope | Effect |
|---|---|---|
| `@AutoMapping` | Class | Opts the DTO into code generation; flags to disable individual artefacts |
| `@MapAs(mapAs = "newName")` | Field | Renames the field in the generated domain/Firebase model |
| `@Ignore` | Field | Excludes field from generated domain and Firebase models |

Generated files land in `app/build/generated/source/kapt/debug/`.

**Do not hand-edit generated files.** Modify the DTO or the processor instead.

### `@AutoMapping` flags

```java
@AutoMapping(
    generateDomainModel  = true,   // default
    generateFirebaseModel = true,  // default
    generateRepository   = true,   // default
    generateContract     = true    // default
)
```

Set a flag to `false` to suppress that artefact (used for `MealDTO` and `MealIngredientDTO`).

---

## Repository & Specification Pattern

```kotlin
// Querying
val meals: List<Meal> = MealRepository().withRealmQuery { AllMealsSortedSpecification(it) }

// Inserting
mealRepo.insert(mealDto, RealmAddTransaction(realm))

// Deleting
mealRepo.delete(MealsByIdsSpecification(realm, ids), RealmRemoveTransaction())
```

`Specification<T>` implementations are in `repo/Specification.kt`. Each encapsulates a Realm query. Add new specifications there when you need a new query.

---

## Firebase Integration

### Database References (`utils.kt`)

```kotlin
val firebaseDb: DatabaseReference  // root reference

// Child references (extension properties)
DatabaseReference.ingredientsRef  // "ingredients"
DatabaseReference.mealsRef        // "meals"
DatabaseReference.usersRef        // "users"
DatabaseReference.tagsRef         // "tags"
```

### FirebaseSaver

All writes go through `FirebaseSaver`. Its methods are synchronous Firebase SDK calls (fire-and-forget). Reads are handled by `valueEventListener {}` DSL in `firebase/FirebaseValueEventListener.kt`.

### Authentication

`MainActivity` reads Firebase credentials from `assets/credentials` (format: `email,password`). This file is **not** committed to git. Create it locally before building.

---

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build directory
./gradlew clean

# Build all modules
./gradlew build
```

There are currently **no unit or instrumented tests** in the project.

---

## Key Conventions

### Naming
- Realm DTOs: `XDto` suffix (e.g., `MealDTO`)
- Firebase models: `Fb` prefix (e.g., `FbMeal`)
- Specifications: `XSpecification` suffix (e.g., `AllMealsSortedSpecification`)
- Screens: `XScreen` suffix; Views: `XView` suffix
- Contract objects: `XContract` suffix (auto-generated)
- Repository classes: `XRepository` suffix (auto-generated or hand-written)

### Language
UI strings are in Polish (the app is for a Polish-speaking family). String resources live in `res/values/strings.xml`. Do not translate or change the language.

### File organisation
- One screen = one `Screen` class + one `View` class
- Utility/extension functions belong in `utils.kt` (top level)
- Screen-specific helpers (interactors, pickers) go in `screens/utils/`
- All Realm transactions must go through the `Transaction` interface pattern

### Realm rules
- Always obtain a Realm instance via `usingRealm { }` (auto-closes) or `realmAsyncTransaction { }` for async writes
- Never access a Realm object outside the thread it was obtained on
- Schema migrations are in `App.kt` → `RealmConfiguration.Builder.migration {}`
- Current schema version: **2**

### StrictMode
`App.kt` enables `StrictMode` (log + death on VM violations). Keep this in mind: avoid disk/network on the main thread.

---

## Meal Types and Categories

### MealType enum
| Value | Polish name | Description |
|---|---|---|
| `MILK` | Mleczko | Milk-based feed |
| `DESSERT` | Deserek | Dessert/snack |
| `DINNER` | Obiadek | Main meal |
| `OTHER` | Przekąska | Other snack |

Each `MealType` carries a list of `IngredientCategory` values used to filter ingredients on the Add Meal screen.

### IngredientCategory enum
`PORRIDGE`, `FRUITS`, `DINNERS`, `OTHERS`, `OILS`, `FRUITS_TUBE`, `DIARY`

---

## Nutritional Fields

The app tracks per-ingredient nutritional data (per 100 g):

| Field | Meaning |
|---|---|
| `mtc` | Medium-chain triglycerides |
| `lct` | Long-chain triglycerides (restricted in LCHAD) |
| `carbohydrates` | Carbohydrates |
| `protein` | Protein |
| `salt` | Salt |
| `roughage` | Dietary fibre |
| `calories` | Total calories |

A **meal completion factor** (0–100%) scales ingredient weights proportionally (e.g., if the child ate 80% of the prepared portion, all weights are multiplied by 0.8).

---

## Dynamic Shortcuts

On Android 7.1+ (API 25+), the app registers a dynamic shortcut for each `MealType` on first launch, allowing the user to jump directly to the Add Meal screen for a specific meal type from the launcher.

---

## Notification Channel

On Android 8.0+ (Oreo), a notification channel `"Default"` is created with a custom sound (`res/raw/mniam.mp3`) for meal notifications delivered via FCM.

---

## Known Technical Debt

1. **`App.kt` still imports old support library** (`android.support.multidex.MultiDexApplication`, `android.support.v4.content.ContextCompat`) — needs migration to `androidx.*`.
2. **`MainActivity.kt` still imports deprecated APIs** (`FirebaseInstanceId`, `SingleActivity` from support Magellan) — Magellan 1.0.0 predates AndroidX; a migration or replacement is pending.
3. **No tests** — the project has no unit or instrumentation tests.
4. **OCR feature** is experimental and incomplete (`OcrActivity`, `OcrDetectorProcessor`, etc.).
5. **Anko references** may still exist in Screen/View classes despite the planned removal.
6. See `TOOLS_UPDATE_PLAN.md` for the full modernisation history and remaining tasks.

---

## Development Branch Convention

Feature/task branches follow the pattern: `claude/<description>-<ID>` (e.g., `claude/add-claude-documentation-WUXHE`). Always push to the specified branch; never push directly to `master`.
