# Werkstatt Infinite

Werkstatt Infinite is a premium Android drawing and notebook app built with Kotlin, Jetpack Compose, StateFlow, Hilt, and Room. It focuses on a clean, low-distraction canvas for sketching, notes, and visual journaling, with fast local persistence and a dark NODAYSIDLE-inspired interface.

The app currently uses a fixed high-resolution drawing page with pinch-to-zoom and two-finger pan. It is not an unbounded infinite canvas yet, but it is designed to feel spacious and smooth on phones without cluttering the drawing surface.

## Features

- Smooth touch drawing with pressure-aware stroke capture
- Pinch-to-zoom and two-finger pan on the canvas
- Clean canvas UI with no left-side tool rail or zoom overlay
- Brush presets for Pen, Fine, Ball, Pencil, Marker, Water, Ink, and Brush
- Brush size, color, eraser, undo, redo, and grid controls
- Local gallery with generated thumbnails
- Auto-save for canvas entries
- Export and share drawings through Android share sheets
- Premium dark theme with Volt accent color

## Current Scope

Werkstatt Infinite is a local-first drawing app. Canvas entries are stored on-device using Room, and generated thumbnails are stored locally for gallery browsing.

Image import is intentionally not part of the active app flow at the moment. The current experience is focused on fast, hassle-free drawing.

## Technical Details

- Language: Kotlin
- UI: Jetpack Compose and Material 3
- Architecture: MVVM with StateFlow and Hilt dependency injection
- Persistence: Room
- Serialization: Gson
- Minimum SDK: 26
- Compile SDK: 36
- Target SDK: 36
- Java/Kotlin target: 17
- Package name: `com.gift.werkstatt`
- Main activity: `com.gift.werkstatt.MainActivity`

The manifest declares a launcher `MainActivity` and a `FileProvider` for sharing exported canvas images. There are no dangerous runtime permissions declared in the current manifest.

## Project Structure

```text
app/src/main/java/com/gift/werkstatt/
  MainActivity.kt
  WerkstattApplication.kt
  core/
    design/
    navigation/
  data/
    files/
    local/
    repository/
    serialization/
  di/
  domain/
    canvas/
  feature/
    editor/
    gallery/
    templates/
  rendering/
    brush/
    export/
    spatial/
    thumbnail/
    transform/
```

Important areas:

- `feature/editor/CanvasEditorScreen.kt` owns the canvas screen layout, export/share flow, and bottom controls.
- `feature/editor/components/DrawingCanvas.kt` handles drawing input, pinch zoom, pan, grid rendering, and canvas drawing.
- `feature/editor/CanvasEditorViewModel.kt` owns editor state, strokes, autosave, eraser logic, undo/redo, zoom state, and brush settings.
- `rendering/brush/StrokeRenderer.kt` renders the different brush styles.
- `data/files/ThumbnailStore.kt` generates lightweight gallery thumbnails.
- `feature/gallery/` owns the gallery grid, sorting, rename, delete, and create-template flow.

## Build

Build a debug APK:

```bash
./gradlew assembleDebug
```

Build a release APK:

```bash
./gradlew assembleRelease
```

The release APK is written to:

```text
app/build/outputs/apk/release/app-release.apk
```

Note: the current release configuration is suitable for local device testing and uses the debug signing config. Use a real release keystore before distributing outside local testing.

## Test And Lint

Run unit tests:

```bash
./gradlew test
```

Run Android lint:

```bash
./gradlew lintDebug
```

Install the release APK on a connected Android device:

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

Launch it with ADB:

```bash
adb shell am start -n com.gift.werkstatt/.MainActivity
```

## Notes For Contributors

- Keep the drawing surface uncluttered.
- Preserve phone-first ergonomics and large touch targets.
- Keep UI strings in Android resources.
- Avoid adding image import back into the active flow unless the product scope changes.
- Do not replace the Android/Kotlin/Compose stack.
