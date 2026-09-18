# LootRPG

Temporary working name for an Android asynchronous loot RPG prototype. Milestone 0
provides engineering foundations and a placeholder screen; gameplay is not built.

Kotlin, Jetpack Compose/Material 3, ViewModel, Coroutines/StateFlow, Room, Gradle
Kotlin DSL, and JUnit. Versions live in `gradle/libs.versions.toml`.

## Prerequisites

- JDK 17 or newer supported by Gradle 9.7.1 (verified locally with JDK 25).
- Android SDK platform 37 and Build Tools 36.0.0; accept SDK licenses.
- Set JAVA_HOME to the JDK and ANDROID_HOME to the SDK. Alternatively set `sdk.dir`
  in an untracked `local.properties`. Never commit machine-specific paths.
- Internet for the first Gradle/dependency download. No global Gradle installation
  or Android Studio is required. VS Code can edit/run all command-line tasks.

PowerShell session setup (replace paths for your machine):

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk'
$env:ANDROID_HOME = 'C:\path\to\Android\Sdk'
```

## Build and verify

```powershell
./gradlew.bat :app:assembleDebug
./gradlew.bat :domain:test :app:testDebugUnitTest
./gradlew.bat :app:lintDebug
# Requires a running emulator or attached device:
./gradlew.bat :app:connectedDebugAndroidTest
```

On macOS/Linux, use `./gradlew` (or `sh gradlew` if executable permissions were not
preserved). JVM domain tests need no emulator. APK:
`app/build/outputs/apk/debug/app-debug.apk`. Reports are under each module's
`build/reports/`; Room schema exports under `app/schemas/` are source-controlled.

## Run

Start an emulator via Android Studio Device Manager or the SDK emulator CLI, or
connect a device with USB debugging. Minimum Android version is API 26.

```powershell
./gradlew.bat :app:installDebug
& "$env:ANDROID_HOME/platform-tools/adb.exe" shell am start -W -n com.example.lootrpg/.MainActivity
```

On macOS/Linux use `adb` from SDK platform-tools. The home screen identifies the
prototype and becomes ready after initialization. Android Studio users may open
the repository, sync Gradle, select `app`, and Run. Use the same supported Gradle
JDK as the command line. Release signing/distribution is outside this milestone.

## Documentation map

- [AGENTS.md](AGENTS.md): permanent repository instructions and targeted context flow.
- [Current state](docs/CURRENT_STATE.md): fast entry point and verification status.
- [Architecture](docs/ARCHITECTURE.md): layer boundaries and package responsibilities.
- [Game design](docs/GAME_DESIGN.md): established concept and explicit TBDs.
- [Data model](docs/DATA_MODEL.md): implemented storage versus future concepts.
- [Roadmap](docs/ROADMAP.md): milestone direction.
- [Decisions](docs/DECISIONS.md): significant choices and rationale.
