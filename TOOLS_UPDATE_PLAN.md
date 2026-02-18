# Tools & Dependencies Update Plan — LchadDietDiary

## Project Overview
Android app (Kotlin) originally written in 2017. All tooling is severely outdated.

---

## Current vs. Target Versions

| Tool / Dependency | Current | Target |
|---|---|---|
| Gradle Wrapper | 4.1 | 8.7 |
| Android Gradle Plugin (AGP) | 3.0.0 | 8.3.2 |
| Kotlin | 1.1.3-2 | 2.1.10 |
| `compileSdkVersion` | 26 | 35 |
| `targetSdkVersion` | 26 | 35 |
| `minSdkVersion` | 16 | 21 |
| `buildToolsVersion` | 26.0.2 | (remove — managed by AGP) |
| Realm | 4.1.0 | 10.18.0 |
| Google Services plugin | 3.1.1 | 4.4.2 |
| Firebase BoM | individual `11.4.2` | BoM `33.x` |
| Anko | 0.10.2 | **Remove** (deprecated; replace with Android KTX) |
| Fabric / Crashlytics SDK | `io.fabric.tools:gradle:1.24.4` + `crashlytics:2.7.1` | **Remove** (deprecated; replace with Firebase Crashlytics) |
| Support Library (`com.android.support`) | 26.0.1 | **Migrate to AndroidX** |
| `multidex` | `com.android.support:multidex:1.0.1` | `androidx.multidex:multidex:2.0.1` |
| MPAndroidChart | v3.0.2 | v3.1.0 |
| Joda Time | `net.danlew:android.joda:2.9.9` | Keep or replace with `java.time` (minSdk ≥ 26) / `ThreeTenABP` |
| KotlinPoet (processor) | 0.6.0 | 1.17.0 |
| Guava (processor) | 22.0 | 33.x |
| `kotlin-stdlib-jre7` | 1.1.3-2 | `kotlin-stdlib` (bundled with Kotlin plugin) |
| `google-services` Fabric Maven | `maven.fabric.io` | Remove |
| `jcenter()` | in use | Replace with `mavenCentral()` |

---

## Step-by-Step Plan

### Step 1 — Create the feature branch
```
git checkout -b claude/plan-app-tools-update-Zlmoz
```

### Step 2 — Update Gradle Wrapper (`gradle/wrapper/gradle-wrapper.properties`)
- Change `distributionUrl` from `gradle-4.1-all.zip` → `gradle-8.7-all.zip`

### Step 3 — Update root `build.gradle`
1. Bump `kotlin_version` from `'1.1.3-2'` → `'2.1.10'`
2. Bump AGP from `'com.android.tools.build:gradle:3.0.0'` → `'8.3.2'`
3. Bump `google-services` from `3.1.1` → `4.4.2`
4. Replace Fabric classpath `io.fabric.tools:gradle:1.24.4` with Firebase Crashlytics Gradle plugin:
   `com.google.firebase:firebase-crashlytics-gradle:3.0.2`
5. Remove `maven { url 'https://maven.fabric.io/public' }` from `buildscript.repositories`
6. Replace `jcenter()` with `mavenCentral()` in both `buildscript.repositories` and `allprojects.repositories`
7. Bump Realm plugin from `4.1.0` → `10.18.0`

### Step 4 — Update `app/build.gradle`

#### Plugins block
- Replace `apply plugin: 'kotlin-android-extensions'` with `buildFeatures { viewBinding true }` (the extensions plugin is deprecated)
- Replace `apply plugin: 'io.fabric'` with `apply plugin: 'com.google.firebase.crashlytics'`

#### Android block
- `compileSdkVersion 26` → `compileSdk 35`
- Remove `buildToolsVersion '26.0.2'` (no longer needed with modern AGP)
- `targetSdkVersion 26` → `targetSdk 35`
- `minSdkVersion 16` → `minSdk 21`
- `testInstrumentationRunner` → `androidx.test.runner.AndroidJUnitRunner`

#### kapt block
- Remove `kapt { generateStubs = true }` (no longer needed in modern kapt/KSP)
- Consider migrating from `kapt` to `ksp` (Kotlin Symbol Processing) for annotation processing, which requires:
  - Adding `id 'com.google.devtools.ksp'` plugin
  - Changing `kapt project(':mapper-proccesor')` → `ksp project(':mapper-proccesor')`

#### Dependencies — replace `compile` with `implementation`/`api` throughout
- `compile` configuration was removed in AGP 7+

#### Dependencies — migrate Support Library → AndroidX
| Old | New |
|---|---|
| `com.android.support:appcompat-v7:26.0.1` | `androidx.appcompat:appcompat:1.7.0` |
| `com.android.support:design:26.0.1` | `com.google.android.material:material:1.12.0` |
| `com.android.support:recyclerview-v7:26.0.1` | `androidx.recyclerview:recyclerview:1.3.2` |
| `com.android.support:multidex:1.0.1` | `androidx.multidex:multidex:2.0.1` |

#### Dependencies — Firebase
Replace individual Firebase/GMS versions with the Firebase BoM for consistent version management:
```groovy
implementation platform('com.google.firebase:firebase-bom:33.8.0')
implementation 'com.google.firebase:firebase-analytics-ktx'
implementation 'com.google.firebase:firebase-database-ktx'
implementation 'com.google.firebase:firebase-messaging-ktx'
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-crashlytics-ktx'
// play-services-vision (if still needed):
implementation 'com.google.android.gms:play-services-vision:20.1.3'
```

#### Dependencies — Remove Anko
Anko (`org.jetbrains.anko`) is abandoned. Replace with:
- `androidx.core:core-ktx:1.15.0` (for Android KTX extensions)
- `com.google.android.material:material:1.12.0` (for Snackbar, etc.)
- Plain Kotlin stdlib for commons utilities

#### Dependencies — Remove Fabric Crashlytics
Remove `com.crashlytics.sdk.android:crashlytics:2.7.1@aar` — Firebase Crashlytics replaces it.

#### Dependencies — other libs
- `com.github.PhilJay:MPAndroidChart:v3.0.2` → `v3.1.0`
- `net.danlew:android.joda:2.9.9` → keep at latest (`2.12.7`) or migrate to `java.time` (since minSdk becomes 21, use `com.jakewharton.threetenabp:threetenabp:1.4.7` for backcompat, or native `java.time` with desugaring enabled)
- `kotlin-stdlib-jre7` → remove explicit declaration (automatically included by Kotlin plugin in modern setups)

#### Remove `maven { url 'https://maven.google.com' }` from `allprojects` — already included via `google()`

### Step 5 — Add `gradle.properties` AndroidX flags
```properties
android.useAndroidX=true
android.enableJetifier=true
```
These enable AndroidX and auto-jetify any remaining support-library transitive deps.

### Step 6 — Enable Java 8+ desugaring (needed for `java.time` on minSdk < 26)
In `app/build.gradle` under `android {}`:
```groovy
compileOptions {
    coreLibraryDesugaringEnabled true
    sourceCompatibility JavaVersion.VERSION_11
    targetCompatibility JavaVersion.VERSION_11
}
kotlinOptions {
    jvmTarget = '11'
}
```
Add dependency:
```groovy
coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.1.4'
```

### Step 7 — Update `mapper-proccesor/build.gradle`
- Bump `com.google.guava:guava:22.0` → `33.3.1-jre`
- Bump `com.squareup:kotlinpoet:0.6.0` → `1.17.0`
- Replace `compile` with `implementation`
- Bump `kotlin-stdlib-jre8` → `kotlin-stdlib` (or remove; provided by plugin)

### Step 8 — Address source-code breakages
After tooling changes, compile errors are expected from:
1. **Anko removal**: replace Anko DSL calls with AndroidX/Material equivalents
2. **`kotlin-android-extensions` removal**: replace synthetic view imports with View Binding
3. **Fabric API removal**: update Crashlytics initialization to `com.google.firebase.crashlytics.FirebaseCrashlytics`
4. **AndroidX migration**: update import paths from `android.support.*` → `androidx.*`
5. **Realm API changes**: Realm 10.x has breaking API changes vs 4.x (configuration, transactions, etc.)

### Step 9 — Run and validate
```
./gradlew assembleDebug
./gradlew test
```
Fix any remaining compilation errors before pushing.

### Step 10 — Commit and push
```
git add -A
git commit -m "Update tooling: AGP 8.3.2, Kotlin 2.1.10, AndroidX migration, Firebase BoM"
git push -u origin claude/plan-app-tools-update-Zlmoz
```

---

## Risk Assessment

| Risk | Severity | Mitigation |
|---|---|---|
| Realm 4→10 breaking API changes | High | Review Realm 10 migration guide; rewrite model/transaction code |
| Anko removal causes widespread UI changes | High | Incrementally replace with View Binding + KTX |
| `kotlin-android-extensions` synthetic imports | Medium | Enable viewBinding and replace imports file-by-file |
| `jcenter` artifacts not on `mavenCentral` | Medium | Check each dep; use JitPack or local JARs as fallback |
| Firebase API changes (11→33) | Medium | Update Crashlytics init; check DB and Auth APIs |
| Magellan library compatibility | Low-Medium | Check if `com.wealthfront:magellan` is on mavenCentral or needs JitPack |

---

## Summary of Files to Change

1. `gradle/wrapper/gradle-wrapper.properties`
2. `build.gradle` (root)
3. `app/build.gradle`
4. `gradle.properties`
5. `mapper-proccesor/build.gradle`
6. Source files (`.kt`) — for Anko removal, view binding, AndroidX imports, Crashlytics init, Realm API
