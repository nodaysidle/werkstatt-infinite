# Werkstatt Fixes Design

## Phase 1: Quick Wins
- Export button in top bar (wire existing exportCanvas function)
- Gallery thumbnails via Coil AsyncImage
- Multi-step undo/redo (30-step stack)
- Brush name display fix (underscores to spaces)

## Phase 2: Image Fixes
- Overlay coordinate mismatch (track canvas size, not container)
- Resize aspect ratio math fix
- Background thread for bitmap decode

## Phase 3: Critical + Polish
- Remove destructive migration
- Dead code cleanup (EntryListScreen, duplicate VM methods)
- Extract magic numbers to constants
- Gallery long-press menu (delete/rename)
- Brush size preview in SlideUpPanel
