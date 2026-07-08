# [PROJECT NAME] — Codex Project Instructions
# Place this file at the root of each repo as: AGENTS.md
# This overrides ~/.codex/AGENTS.md for anything listed here.
# Delete sections you don't need. Keep it tight.

---

## Project

- **Name:** [Your App Name]
- **Platform:** [Android / Flutter / Web / Node / etc.]
- **Status:** [Active / Finishing / Maintenance]

---

## Stack

- [e.g. Kotlin + Jetpack Compose, Min SDK 26, Target SDK 35]
- [e.g. Flutter 3.x + Riverpod + Freezed]
- [e.g. Next.js 14 + TypeScript + Tailwind]

---

## Build & Test Commands

```bash
# Build
[e.g. ./gradlew assembleDebug]

# Test
[e.g. ./gradlew test]

# Lint
[e.g. ./gradlew lint]
```

---

## Architecture

- [e.g. MVVM + StateFlow + Hilt — do not change the pattern]
- [e.g. Repository layer handles all data access — no direct API calls from UI]
- [e.g. Single Activity, Compose navigation only]

---

## Critical Constraints

- [e.g. Never access Context inside a ViewModel]
- [e.g. All strings in strings.xml — no hardcoded text in Compose]
- [e.g. ProGuard rules must be reviewed when adding any new library]
- [e.g. Do not modify the payment module without explicit instruction]

---

## Off-Limits

- [e.g. Do not touch /legacy — it is frozen]
- [e.g. Do not upgrade AGP or Gradle without asking]
- [e.g. Do not rename any public API surface]

---

## Notes for Codex

- [Any project-specific quirks, known issues, or context Codex needs]
- [e.g. The `compat/` folder exists for a reason — do not clean it up]
