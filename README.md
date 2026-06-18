# First Phone

Turn any Android phone into a safe first phone for kids aged 5–10.

First Phone is a **native Android** parental-control launcher add-on built with:

- Kotlin · Jetpack Compose · Material 3
- MVVM + Repository pattern
- Hilt (Dagger) Dependency Injection
- Room Database (offline-only, no cloud)
- WorkManager (daily reset + periodic usage sync)
- Android **Accessibility Service** (real-time blocking)
- Android **Usage Stats API** (per-app time tracking)
- **Device Admin** API (uninstall protection)

The app is offline-first, parent-friendly, and contains **no analytics, ads, or cloud features**.

---

## Concept

- **Always Allowed Apps** — Phone, Contacts, Camera, WhatsApp, SMS, Clock, Calculator, etc. Usable any time, no limit.
- **Vault Apps** — YouTube, Instagram, Chrome, Play Store, games, etc. They share a **single daily timer** (default 18 minutes).
- When the Vault timer runs out, opening any Vault app shows a full-screen **Vault Closed** block screen until tomorrow.
- Parents can grant extra time via **Quick Override**, soften weekends via **Weekend Mode**, or set a date range via **Vacation Mode**.

---

## Project Layout

```
FirstPhone/
├── build.gradle.kts              ← top-level
├── settings.gradle.kts
├── gradle/libs.versions.toml     ← version catalog
├── gradle.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/
        │   ├── values/{strings,colors,themes}.xml
        │   ├── xml/{device_admin,accessibility_service_config,data_extraction_rules}.xml
        │   └── mipmap-anydpi-v26/{ic_launcher,ic_launcher_round}.xml
        └── java/com/firstphone/app/
            ├── FirstPhoneApplication.kt
            ├── MainActivity.kt
            ├── activity/BlockActivity.kt
            ├── di/DatabaseModule.kt
            ├── data/
            │   ├── local/{FirstPhoneDatabase, dao/*, entities/*}.kt
            │   └── repository/{Settings,App,Usage}Repository.kt
            ├── domain/
            │   ├── model/AppInfo.kt
            │   └── usecase/{UsageTracker, VaultStatusCalculator}.kt
            ├── service/
            │   ├── AppBlockerAccessibilityService.kt
            │   ├── UsageMonitorService.kt
            │   ├── FirstPhoneDeviceAdminReceiver.kt
            │   └── BootReceiver.kt
            ├── worker/
            │   ├── DailyResetWorker.kt
            │   ├── UsageSyncWorker.kt
            │   └── WorkScheduler.kt
            ├── ui/
            │   ├── theme/{Color, Theme, Type}.kt
            │   ├── navigation/{Routes, FirstPhoneNavGraph}.kt
            │   ├── components/{PinInput, Buttons, SectionCard}.kt
            │   └── screens/
            │       ├── welcome/WelcomeScreen.kt
            │       ├── pin/Create{Pin,Screen,ViewModel}.kt
            │       ├── permissions/Permissions{Screen,ViewModel}.kt
            │       ├── appselection/{AppSelectionScreens, AppSelectionViewModel}.kt
            │       ├── timelimit/Choose{TimeScreen, TimeLimitViewModel}.kt
            │       ├── dashboard/{Dashboard,Setup}*.kt
            │       └── parent/{ParentGate, ParentSettings, ChangePin, WeekendMode, VacationMode, Override}*.kt
            └── util/
                ├── Constants.kt
                ├── PinHasher.kt
                ├── PermissionChecker.kt
                ├── InstalledAppsProvider.kt
                └── ScheduleHelper.kt
```

---

## Setup in Android Studio

1. Install **Android Studio Koala Feature Drop (2024.1.2)** or newer.
2. `File ▸ Open…` → select the `FirstPhone/` folder.
3. Allow Gradle sync to finish (downloads AGP 8.5.2 / Kotlin 2.0.20 / Hilt 2.52).
4. Plug in a real Android device (Android 8.0, API 26 or higher).  
   The Usage Stats API and Device Admin do **not** function on most emulators.
5. `Run ▸ Run 'app'`.

### Build from CLI (optional)

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

### Build an APK in the cloud (no local setup)

This project ships with a GitHub Actions workflow at
`.github/workflows/android.yml` that builds `app-debug.apk` on every push and
exposes it as a downloadable artifact. See **[BUILDING_APK.md](BUILDING_APK.md)**
for the full guide.

TL;DR:

1. Push the project to GitHub.
2. Open **Actions** tab → workflow auto-runs (~6–10 min on first run).
3. Download `app-debug-apk` artifact from the completed run.
4. Unzip → `adb install app-debug.apk`.

---

## First-Run Flow

1. **Welcome** — `GET STARTED`.
2. **Create Parent PIN** — 4 digits, hashed with PBKDF2-SHA256 + per-device salt before storage in Room.
3. **Permissions Setup** — three live status indicators:
   - Usage Access (`Settings ▸ Apps ▸ Special access ▸ Usage access`)
   - Accessibility (`Settings ▸ Accessibility ▸ First Phone Vault`)
   - Device Administrator (system prompt invoked by the app)
   The `CONTINUE` button stays disabled until all three are granted; the screen polls every 800 ms.
4. **Always Allowed Apps** — preselects Phone/Contacts/Camera/WhatsApp/SMS/Clock/Calculator + their AOSP variants.
5. **Vault Apps** — pick the apps that should share the daily timer.
6. **Choose Daily Vault Time** — 18 / 30 / 45 / 60 min (default 18).
7. **Dashboard** — status, remaining time, used today, protected app count, current schedule, settings gear.

---

## How Blocking Works

`AppBlockerAccessibilityService` listens for `TYPE_WINDOW_STATE_CHANGED` events. When a Vault app comes to the foreground **and** `VaultStatusCalculator.shouldBlockNow()` returns `true`, the service launches `BlockActivity` (full-screen, `singleInstance`, transparent task affinity). `BlockActivity` sends the user back to the launcher via the `CATEGORY_HOME` intent on dismiss or back-press.

`UsageMonitorService` is a low-priority foreground service that polls `UsageStatsManager.queryEvents()` every 5 seconds, accumulates Vault usage into Room, and refreshes the dashboard. `UsageSyncWorker` is a periodic WorkManager fallback every 15 minutes for when the foreground service is killed.

`DailyResetWorker` is scheduled at midnight to clear counters and overrides.

---

## Parent Settings (PIN gated)

- **Change PIN** — replaces hash with new PBKDF2 derivation.
- **Edit Always Allowed Apps / Edit Vault Apps** — re-uses the setup screens.
- **Change Daily Limits** — re-uses Choose Time.
- **Weekend Mode** — independent Saturday & Sunday limits (`Same as weekday` / 30 / 45 / 60 / Unlimited).
- **Vacation Mode** — start date, end date, limit (30 / 60 / 90 / Unlimited). Automatically reverts after the end date.
- **Quick Override** — adds +15 / +30 / +60 min or `Unlimited` to **today only**. Resets at midnight.

After **3 wrong PIN attempts** the parent gate locks for **15 minutes** (`PIN_LOCKOUT_MS`).

---

## Uninstall Protection

`FirstPhoneDeviceAdminReceiver` is registered with the `force-lock`, `watch-login`, and `disable-keyguard-features` policies. While Device Admin is active, Android prevents `pm uninstall` and the standard uninstaller path. To remove First Phone the parent must:

1. Open First Phone → `Parent Settings` → enter PIN.
2. `Settings ▸ Security ▸ Device admin apps ▸ First Phone Protection ▸ Deactivate` (Android requires this manually for safety).
3. Then uninstall through the system.

If Accessibility or Usage Access is revoked, the dashboard will show **PAUSED** and a permissions banner; the parent must restore them through the PIN-gated permissions screen.

---

## Required Manifest Declarations (already configured)

- `PACKAGE_USAGE_STATS` (special access, granted via system Settings UI)
- `QUERY_ALL_PACKAGES` (read installed-app list on Android 11+)
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`
- `POST_NOTIFICATIONS` (Android 13+, runtime prompt at first launch)
- `RECEIVE_BOOT_COMPLETED`
- `BIND_ACCESSIBILITY_SERVICE` (on the `<service>` itself)
- `BIND_DEVICE_ADMIN` (on the `<receiver>` itself)

---

## Local Data Storage

All data is local. Room DB `first_phone.db` with 3 tables:

| Table         | Purpose                                                       |
| ------------- | ------------------------------------------------------------- |
| `settings`    | Single-row config (PIN hash/salt, limits, vacation, override) |
| `managed_apps`| Discovered apps + `isAlwaysAllowed`/`isVault` flags           |
| `usage_log`   | Per-day per-package usage (ms)                                |

No data leaves the device. Backup is explicitly disabled (`android:allowBackup="false"` + `data_extraction_rules.xml`).

---

## Privacy Statement (for Accessibility Service description)

> First Phone uses the Accessibility Service only to detect which app is in the foreground and overlay a block screen when the daily Vault limit is reached. It does **not** read screen content, capture keystrokes, record passwords, or transmit any data off the device.

This is also visible to the user in `Settings ▸ Accessibility ▸ First Phone Vault`.

---

## Testing Checklist (manual, requires a real device)

1. Complete first-run flow in under 5 minutes.
2. Verify Permissions screen status updates within ~1 sec of toggling each permission.
3. Confirm Phone / WhatsApp / SMS / Camera are usable without limit.
4. Open YouTube (or another Vault app) repeatedly: counter on dashboard ticks up in seconds.
5. Set the daily limit to 1 minute via Parent Settings; spend ~60 sec in a Vault app; confirm the **Vault Closed** screen takes over the foreground.
6. Press Back on Block screen → launcher appears.
7. Re-open the same Vault app → Block screen reappears.
8. Use **Quick Override → Add 15 minutes today**; Vault apps become usable again.
9. Set time on the device forward to next-day; Vault timer resets to full at midnight (or wait for `DailyResetWorker`).
10. Enter wrong PIN 3× at Parent Gate → lockout for 15 minutes.
11. Try to uninstall First Phone → Android refuses because Device Admin is active.

---

## License

This project is provided as-is for educational and personal use.
