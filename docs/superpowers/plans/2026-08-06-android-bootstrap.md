# Smartvisor Android Bootstrap Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create a real Android project that builds an installable APK through GitHub Actions.

**Architecture:** A single Android application module using Kotlin and Material Components. The first milestone provides a stable home screen, package identity, versioning, and CI-generated debug APK; later features will be migrated incrementally.

**Tech Stack:** Kotlin, Android Gradle Plugin, AndroidX AppCompat, Material Components, GitHub Actions.

## Global Constraints

- Package ID: `com.smartvisor.android`
- Minimum Android version: API 26
- Target/compile SDK: API 35
- JDK: 17
- APK must be produced by GitHub Actions from repository source.
- No placeholder APK files may be distributed.

---

### Task 1: Bootstrap project
- Create Gradle settings, root build, app module, resources, and manifest.
- Add a real launcher activity with a usable Smartvisor home screen.

### Task 2: Add APK build workflow
- Configure GitHub Actions with JDK 17 and Gradle.
- Run unit tests and `assembleDebug`.
- Upload `app-debug.apk` as an artifact.

### Task 3: Verify
- Confirm workflow success.
- Download the generated artifact and provide only the actual APK.
