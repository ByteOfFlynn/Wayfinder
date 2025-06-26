# Wayfinder — Android Treasure Hunt App

**Wayfinder** is a location-based mobile application built in **Android Studio** using **Kotlin** and **Jetpack Compose**. The app simulates a real-world treasure hunt experience by guiding users through a series of geolocation-based clues. The primary focus is on integrating location services, asynchronous event handling, and modern Android UI patterns, all packaged into a fluid, interactive mobile application.

## Overview

This project showcases the development of a multi-screen, GPS-aware Android app that tracks user movement and location proximity to solve a sequential set of clues. The user experience is built around permissions management, animated state transitions, distance calculations, and responsive feedback to real-world interaction.

## Core Features

###  Location Awareness

- Real-time GPS tracking using **Fused Location Provider**
- Location validation via the **Haversine formula**
- Accuracy tolerance management to account for real-world signal drift

###  Clue-Based Progression

- Sequential clue delivery with one clue active at a time
- Clues loaded from a local **resource file** for consistency and offline functionality
- Optional **hint system** for user assistance

###  Timer System

- Animated count-up timer visible throughout the active session
- Timer paused during informational interludes but total runtime tracked accurately

###  UI Screens

- **Permissions Screen** — First-time location permission handling with clear UX flow
- **Start Screen** — Scrollable rules overview and entry point to the experience
- **Clue Screen** — Active clue, hint button, "Found It" logic, quit option, live timer
- **Clue Solved Screen** — Informational view with paused timer and Continue control
- **Treasure Found Screen** — Completion view showing total time and final message

###  UI/UX

- Built with **Jetpack Compose**
- Clean, composable screen architecture using **StateFlow** and **ViewModel** for logic separation
- Declarative layouts for flexible visual updates
- Responsive navigation through all app states

###  Permissions Handling

- Structured onboarding screen for requesting fine and coarse location access
- Graceful handling of denied permissions with guided UI prompts

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **IDE**: Android Studio
- **Architecture**: MVVM with ViewModel + StateFlow
- **Location Services**: Google Play Services Location APIs
- **Testing Environment**: Android Emulator with mock GPS data

## Architecture Highlights

- **Composable Screens**: All views are structured using Kotlin composables
- **Reactive State Management**: App state and flow controlled via StateFlow
- **Resource-Based Content**: All gameplay text is loaded from bundled resources
- **Distance Calculation**: Implements Haversine formula for accurate geospatial validation
- **Lifecycle-Aware Components**: ViewModels persist state across configuration changes

## Emulator & Testing

- Fully functional in Android Emulator with support for mock GPS input
- Test routes and single-point GPS injection via Android Studio Extended Controls
- Graceful fallback behavior for invalid locations or edge-case interactions
