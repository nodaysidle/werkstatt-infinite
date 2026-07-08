# AGENTS.md

## Project: Werkstatt Infinite

## Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM with StateFlow (UDF)
- **DI:** Hilt
- **Persistence:** Room
- **Serialization:** Gson
- **Build:** Gradle Kotlin DSL + AGP 8.9.1
- **Min SDK:** 26 · **Target SDK:** 36

## Key Rules
- Use `com.gift.werkstatt` package namespace
- Follow MAD 3-layer architecture: domain → data → feature
- ViewModels expose `StateFlow<UiState>`, screens use `collectAsStateWithLifecycle()`
- All custom data classes in Compose state must be `@Immutable` or `@Stable`
- Use `SavedStateHandle` for navigation args in ViewModels
- Room entities live in `data/local/entity/`, DAOs in `data/local/dao/`
- Brush rendering: native fast path (Pen/Fine/Ballpoint/Marker) via `android.graphics.Canvas`; Compose path for others
- Canvas dimensions: 3000×4500 fixed resolution with viewport transform (zoom 0.5×–5×)
- Grid modes: None / Lines (drawLine) / Dots (point-mode with step multiplier)
- Theme: minimalistic dark void — deep void palette (not pure black), Volt `#C8FF00` accent, monospace labels
- UI must stay minimal and uncluttered — no unnecessary chrome, decorations, or visual noise
- UI strings in Android resources (`res/values/strings.xml`)
- Auto-save: 1.8s debounce, throttled thumbnail refresh every 5s

## Do NOT
- Replace Kotlin/Compose with any other stack
- Replace Room with any other persistence library
- Add image import back into the active flow
- Add dangerous runtime permissions without explicit product scope change
- Use `composeOptions { kotlinCompilerExtensionVersion }` — the Compose compiler plugin replaces it
- Add dependencies without updating this file
- Clutter the drawing surface — keep the canvas uncluttered
- Ship release APKs with the debug signing config
- Use pure black (`#000000`) — dark void palette only

## Doc Structure
1. **AGENTS.md** — Global rules and stack constraints (READ FIRST)
2. **PRD.md** — Product vision, goals, non-goals
3. **ARD.md** — Architecture decisions and trade-offs
4. **TRD.md** — API contracts, data models, module interfaces
5. **TASKS.md** — Phased task breakdown

## Build Reference

```bash
./gradlew test            # Run unit tests
./gradlew lintDebug       # Run Android lint
./gradlew assembleDebug   # Build debug APK → app/build/outputs/apk/debug/
./gradlew assembleRelease # Build release APK (uses debug keystore — replace before distribution)
```

## Key files

| Path | Purpose |
|------|---------|
| `app/src/main/java/com/gift/werkstatt/MainActivity.kt` | Single Activity entry point |
| `app/src/main/java/com/gift/werkstatt/WerkstattApplication.kt` | Hilt application |
| `app/src/main/java/com/gift/werkstatt/core/design/WerkstattTheme.kt` | Theme, colors, typography |
| `app/src/main/java/com/gift/werkstatt/feature/editor/CanvasEditorViewModel.kt` | Canvas editor state + stroke logic |
| `app/src/main/java/com/gift/werkstatt/feature/editor/components/DrawingCanvas.kt` | Canvas composable + touch handling |
| `app/src/main/java/com/gift/werkstatt/rendering/brush/StrokeRenderer.kt` | 8 brush types, native + Compose paths |
| `app/src/main/java/com/gift/werkstatt/rendering/transform/CanvasTransform.kt` | Pinch-zoom/pan transform |
| `app/src/main/java/com/gift/werkstatt/domain/canvas/model/CanvasModels.kt` | Domain models, brush presets, palettes |
| `app/src/main/java/com/gift/werkstatt/data/local/db/WerkstattDatabase.kt` | Room database + migrations |
| `app/build.gradle.kts` | Dependencies, build config |
