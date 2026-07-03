# Project Plan

DeathNote: A premium, minimalist, and privacy-focused journaling and note-taking app with a "Monolith Noir" design. Features include hierarchical notes (Notebooks/Sections/Pages), date-driven journaling, biometric security, global search, and hybrid cloud sync (Firebase/Room). Built with Kotlin and Jetpack Compose.

## Project Brief

# Project Brief: DeathNote

DeathNote is a premium, minimalist, and privacy-focused journaling application. It combines a "Monolith Noir" aesthetic—characterized by deep blacks, crisp white typography, and glowing red accents—with a futuristic, highly structured organization system.

## Features
- **Monolith Noir Journaling:** Create date-driven entries with automatic day detection, wrapped in a high-contrast, futuristic interface inspired by Nothing OS.
- **Hierarchical Note Structure:** Organize thoughts through a nested architecture of Notebooks, Sections, and Pages for professional-grade documentation.
- **Biometric Privacy Shield:** Secure sensitive notebooks with mandatory biometric (Fingerprint/Face) or passcode authentication.
- **Hybrid Cloud Sync:** Support for both a local-first "Guest Mode" and a Firebase-backed synchronized experience for authenticated users.
- **Global Omni-Search:** A high-performance search engine to instantly locate entries, keywords, or specific dates across the entire note hierarchy.

## High-Level Technical Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Navigation:** Jetpack Navigation (State-driven)
- **Adaptive Layouts:** Compose Material Adaptive library (supporting foldables, tablets, and handsets)
- **Concurrency:** Kotlin Coroutines & Flow
- **Architecture:** MVVM with Clean Architecture principles
- **Backend/Sync:** Firebase (Authentication & Firestore)
- **Local Persistence:** Room Database (for offline-first capability)
- **Dependency Injection:** Hilt

## UI Design Image
![UI Design](file:///C:/Users/Noor%20Mohammad/AndroidStudioProjects/DeathNote/input_images/image_0.png)

## Implementation Steps

### Task_1_Infrastructure_DataLayer: Initialize Hilt, Room (Notebook, Section, Page, Journal entities), and DataStore. Set up the core Clean Architecture layers (Data, Domain, UI).
- **Status:** COMPLETED
- **Updates:** Infrastructure and data layer setup is complete.
- **Acceptance Criteria:**
  - Hilt is integrated and app builds
  - Room database schema handles hierarchical notes and journal entries
  - Project compiles successfully

### Task_2_Theme_Navigation_Dashboard: Implement the Monolith Noir theme (Dark/Light M3) and set up Navigation3. Build the primary Dashboard for journaling and note hierarchy.
- **Status:** COMPLETED
- **Updates:** Implemented the Monolith Noir theme (Dark/Light M3) and set up Jetpack Compose Navigation.
- **Acceptance Criteria:**
  - UI matches the design in input_images/image_0.png
  - Edge-to-Edge display is enabled
  - Navigation between Journal and Notebook views works
  - The implemented UI must match the design provided in input_images/image_0.png

### Task_3_Editor_and_Biometrics: Implement the rich text editor for entries and integrate biometric authentication (Fingerprint/Face) for secured notebooks.
- **Status:** COMPLETED
- **Updates:** Implemented the journaling entry editor and integrated biometric authentication.
- **Acceptance Criteria:**
  - Users can create and edit entries with date-driven logic
  - Biometric prompt secures designated notebooks
  - The implemented UI must match the design provided in input_images/image_1.png

### Task_4_Search_Sync_and_Assets: Implement Global Omni-Search and Firebase Firestore synchronization. Create an adaptive app icon.
- **Status:** COMPLETED
- **Updates:** Implemented Global Omni-Search, Firebase Firestore synchronization, and an adaptive app icon.
- **Acceptance Criteria:**
  - Global search returns relevant results instantly
  - Data syncs with Firebase/Firestore
  - Adaptive app icon is correctly implemented

### Task_5_Final_Verification: Perform a final build, UI polish, and stability check. Ensure all features align with the Monolith Noir aesthetic.
- **Status:** COMPLETED
- **Updates:** Final build, UI polish, and stability check completed.
- Critic agent verified the app's structure, logic, and aesthetic.
- Monolith Noir design system is consistently applied across all screens.
- Core features (Journaling, Notebook hierarchy, Biometrics, Search, Firebase Sync) are fully implemented.
- Adaptive app icon is correctly designed and integrated.
- Build passed successfully.
- Code follows Clean Architecture and modern Android best practices.
- **Acceptance Criteria:**
  - App does not crash during navigation or data entry
  - All existing tests pass
  - Build pass
  - Critic agent confirms alignment with requirements and design images
- **Duration:** N/A

