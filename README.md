<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/logo.svg">
    <img alt="Werkstatt Infinite" src="docs/logo.svg" width="180">
  </picture>
</p>

<p align="center">
  <strong>W E R K S T A T T &ensp; I N F I N I T E</strong>
</p>

<p align="center">
  Premium Android drawing and notebook app. Local-first. Jetpack Compose. Dark void.
</p>

<p align="center">
  <a href="#download">Download</a> ·
  <a href="#features">Features</a> ·
  <a href="#architecture">Architecture</a> ·
  <a href="#build">Build</a>
</p>

---

## What is Werkstatt Infinite?

Werkstatt Infinite is a premium Android drawing and bullet-journaling app built with Kotlin, Jetpack Compose, and Room. It focuses on a clean, low-distraction canvas for sketching, notes, and visual journaling — with fast local persistence, a dark void aesthetic, and NODAYSIDLE's Volt accent.

No accounts. No cloud. No clutter. Just you and the canvas.

## Features

- **8 brush types** — Pen, Fine, Ballpoint, Pencil, Marker, Watercolor, Ink, Brush — each with distinct rendering (native fast path + Compose path)
- **Pressure-aware strokes** — captures Android pointer pressure for natural line weight
- **Pinch-to-zoom and two-finger pan** — fluid viewport transform (0.5×–5× zoom)
- **Grid system** — toggle lines, dots, or off with smart step multiplier at any zoom level
- **Undo / redo** — full stroke-level history
- **Eraser** — spatial-index-backed, fast even with 10,000+ strokes
- **Color wheel + 6 preset palettes** — Bold, Pastel, Earth, Neon, Skin, Vintage
- **Gallery with thumbnails** — auto-generated, sort by date or name, rename, delete
- **Auto-save** — 1.8-second debounce, throttled thumbnail refresh
- **Export** — share drawings through Android share sheets
- **Paper templates** — Blank, Ruled, Dot Grid, Small Grid, Storyboard, Sketch

## Download

Download the latest APK from GitHub Releases:

[Download latest release](https://github.com/nodaysidle/werkstatt-infinite/releases/latest)

Requirements:

- Android 8.0 (API 26) or newer
- ~50 MB free space

Install:

```bash
adb install werkstatt-infinite.apk
```

Or transfer the APK to your device and open it.

## Keyboard shortcuts (Bluetooth keyboard)

| Shortcut | Action |
|----------|--------|
| `Ctrl + Z` | Undo |
| `Ctrl + Shift + Z` | Redo |
| `Ctrl + G` | Cycle grid mode |
| `Ctrl + E` | Toggle eraser |
| `Ctrl + S` | Save now |
| `Back` | Return to gallery |

## Architecture

```text
app/src/main/java/com/gift/werkstatt/
  MainActivity.kt              Single Activity entry point
  WerkstattApplication.kt      Hilt application
  core/
    design/                    Theme, colors (dark void + Volt #C8FF00), typography
    navigation/                Type-safe NavHost + routes
  data/
    files/                     Canvas export store, thumbnail store
    local/                     Room entities, DAOs, database + migrations
    repository/                Repository implementation
    serialization/             Gson JSON codec for stroke data
  di/                          Hilt module — Room, Gson, DAOs
  domain/
    canvas/
      model/                   CanvasEntry, Stroke, BrushType, BrushPresets, GridMode, palettes
      repository/              Repository interface
      usecase/                 BuildStroke, EraseStrokes, GetCanvas, SaveCanvas, DeleteCanvas
  feature/
    editor/                    Canvas screen, ViewModel, DrawingCanvas, gesture handler
    gallery/                   Gallery grid, sort, rename, delete dialogs
    templates/                 Paper template picker
  rendering/
    brush/                     StrokeRenderer — 8 brush types, native + Compose paths
    export/                    Canvas-to-bitmap export renderer
    spatial/                   StrokeSpatialIndex — fast hit-testing for eraser
    thumbnail/                 Thumbnail renderer
    transform/                 Pinch-zoom/pan coordinate mapper
```

Tech stack:

- Kotlin
- Jetpack Compose + Material 3
- MVVM with StateFlow (UDF)
- Hilt dependency injection
- Room (SQLite)
- Gson serialization
- Gradle Kotlin DSL + AGP 8.9.1
- Min SDK 26 · Target SDK 36

## Build from source

```bash
git clone https://github.com/nodaysidle/werkstatt-infinite.git
cd werkstatt-infinite
```

Create `local.properties`:

```properties
sdk.dir=/Users/youruser/Library/Android/sdk
```

Build debug APK:

```bash
./gradlew assembleDebug
```

Run tests:

```bash
./gradlew test
```

Run lint:

```bash
./gradlew lintDebug
```

Install on device:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Release verification

```bash
./gradlew test                        # Unit tests
./gradlew lintDebug                   # Static analysis
./gradlew assembleDebug               # Debug APK
apksigner verify --verbose app-debug.apk   # Signature check
apkanalyzer apk summary app-debug.apk       # Package check
```

## Privacy

Werkstatt Infinite is local-first:

- No account system
- No telemetry
- No network calls
- All canvas data stays on-device in Room/SQLite
- No dangerous runtime permissions

## License

MIT

---

<p align="center">
  Built by <a href="https://github.com/nodaysidle">NODAYSIDLE</a>
</p>
