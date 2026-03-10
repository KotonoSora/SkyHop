# 🎵 SkyHop: Audio (BGM & SFX) Master Implementation Specification (v2.0)
**Role**: Technical Specification & Execution Roadmap  
**Status**: CTO Approved - Ready for Engineering

---

## 1. Executive Summary
This document serves as the single source of truth for integrating a comprehensive Audio System into SkyHop. It defines the architecture required to deliver a seamless Background Music (BGM) and interactive Sound Effects (SFX) experience that respects user preferences and device lifecycle events.

The strategy relies on three core pillars:
1. **Persistent State Management**: Reliable storage and observation of user audio preferences via DataStore for both BGM and SFX independently.
2. **Isolated Audio Engine**: A decoupled service layer (`AudioManager`) managing raw Android media APIs (`MediaPlayer` for BGM, `SoundPool` for SFX) to prevent UI contamination.
3. **Lifecycle-Aware UI Integration**: Ensuring BGM strictly aligns with screen navigation/visibility, and SFX are triggered responsively on specific gameplay events (start, touch, game over).

---

## 2. Architecture & Dependencies

### 2.1 Component Stack
To guarantee stability and separation of concerns, the following components are utilized:
*   **Data Layer**: `androidx.datastore:datastore-preferences` (Existing `SettingsRepository`).
*   **Audio Service**: 
    *   `android.media.MediaPlayer` (Ideal for long-running, looping Background Music).
    *   `android.media.SoundPool` (Ideal for low-latency, rapid-fire Sound Effects).
    *   `AudioManager` (Wrapper class for centralized control and AudioFocus management).
*   **UI Layer**: Jetpack Compose `DisposableEffect` and `LifecycleEventObserver`.

### 2.2 Pre-flight Checklist
| Status | Step | Action |
|:---:|:---|:---|
| [ ] | **2.2.1** | Create `app/src/main/res/raw/` directory. |
| [ ] | **2.2.2** | Add BGM asset (`bg_music.mp3`) and SFX assets (`sfx_start.ogg`, `sfx_touch.ogg`, `sfx_game_over.ogg`) to `raw/`. |
| [ ] | **2.2.3** | Define localization strings (`label_music`, `desc_music_toggle`, `label_sfx`, `desc_sfx_toggle`) in `strings.xml`. |

### 2.3 Audio Asset Guidelines (CTO Recommendations)
To ensure commercial safety, zero-latency playback, and a cohesive game feel, adhere to the following asset sourcing rules:
*   **Licensing**: Strictly use **CC0 (Public Domain)** assets to avoid copyright strikes and complex attribution. Recommended trusted sources: **Kenney.nl** (search "UI Audio" or "Jumps"), **JFXR/sfxr** (for 8-bit generated sounds), or **Pixabay Audio / OpenGameArt** (for BGM).
*   **Format Optimization**: 
    *   **BGM (`bg_music`)**: `.mp3` format is acceptable for `MediaPlayer`. Must be a seamless looping track.
    *   **SFX (`sfx_*`)**: Must be converted to **`.ogg`** format. Android's `SoundPool` decodes `.ogg` significantly faster than MP3, ensuring zero-latency playback the exact millisecond a player taps the screen.
*   **Aesthetic & Vibe Mapping**:
    *   `sfx_start.ogg`: Uplifting and energetic transition (e.g., "Swoosh up", "Level start chime").
    *   `sfx_touch.ogg`: **Critical.** Must be extremely short (< 0.2s), low-volume, and non-fatiguing since it plays constantly. (e.g., "Bubble pop", "Soft flap", "Muted tap"). Avoid sharp clicks.
    *   `sfx_game_over.ogg`: Clear but not overly punishing failure indicator (e.g., "Low thud", "Descending synth").
    *   *(Future)* `sfx_collect.ogg`: A bright, rewarding "Ping!" for passing pipes/collecting coins.

---

## 3. Audio Engine & State Management Specifications

### 3.1 Persistence Schema (`SettingsRepository`)
| Key Name | Type | Default Value | Description |
|---|---|---|---|
| `MUSIC_ENABLED_KEY` | `booleanPreferencesKey` | `true` | Master toggle for background music playback. |
| `SFX_ENABLED_KEY` | `booleanPreferencesKey` | `true` | Master toggle for sound effects. |

### 3.2 Audio Service Capabilities (`AudioManager`)
The isolated engine must handle the following state transitions to avoid memory leaks or audio latency:
*   **`initialize(context)`**: 
    *   Prepare `MediaPlayer` with `R.raw.bg_music` and configure `isLooping = true`.
    *   Initialize `SoundPool` and load `sfx_start`, `sfx_touch`, and `sfx_game_over` into memory.
*   **BGM Controls**: `playBgm()`, `pauseBgm()`, `stopBgm()`.
*   **SFX Controls**: `playSfx(SfxType)` - Triggers a short sound with minimal latency if `SFX_ENABLED` is true. `SfxType` enum maps to (START, TOUCH, GAMEOVER).
*   **`release()`**: Free native resources (`MediaPlayer` and `SoundPool`) when the component is permanently destroyed.

### 3.3 SFX Trigger Map
| Event | Trigger Context | Target SFX | Target UI/ViewModel Function |
|---|---|---|---|
| **Game Start** | User initiates a new run. | `sfx_start` | `GameViewModel.startGame()` |
| **Screen Touch** | User taps screen during active gameplay. | `sfx_touch` | `GameScreen` -> Tap Modifier |
| **Game Over** | Player collides with obstacle/boundary. | `sfx_game_over` | `GameViewModel.checkAndHandleGameOver()` |

---

## 4. Micro-Task Execution Roadmap (Max 0.5h/Task)

| Phase | Task ID | Target Component | Task Description | Est. Time |
|---|---|---|---|---|
| **P1: Init** | 1.1 | Resources | Ensure `res/raw` exists, add all 4 audio files, and update `strings.xml`. | 0.5h |
| **P2: Data** | 2.1 | `SettingsRepository` | Define `MUSIC_ENABLED_KEY` & `SFX_ENABLED_KEY`; expose both as StateFlows. | 0.5h |
| | 2.2 | `SettingsRepository` | Implement `toggleMusic(enabled)` and `toggleSfx(enabled)` suspend functions. | 0.5h |
| **P3: Engine**| 3.1 | `AudioManager` | Create Singleton; implement `initialize` with `MediaPlayer` and `SoundPool`. | 1.0h |
| | 3.2 | `AudioManager` | Implement BGM lifecycle (`playBgm`, `pauseBgm`, `stopBgm`, `release`). | 0.5h |
| | 3.3 | `AudioManager` | Implement SFX logic (`playSfx(type)` checking internal cache and user toggle). | 0.5h |
| **P4: Logic** | 4.1 | `GameViewModel` | Observe `musicEnabledFlow` and `sfxEnabledFlow` to gate playback logic. | 0.5h |
| | 4.2 | `GameViewModel` | Orchestrate BGM/SFX on game start (`playBgm`, `playSfx(START)`). | 0.5h |
| | 4.3 | `GameViewModel` | Orchestrate BGM/SFX on game over (`stopBgm`, `playSfx(GAMEOVER)`). | 0.5h |
| **P5: UI** | 5.1 | `GameScreen` | Trigger `playSfx(TOUCH)` directly from the game screen's tap modifier. | 0.5h |
| | 5.2 | `GameScreen` | Implement `DisposableEffect` for `AudioManager.stopBgm()` component cleanup. | 0.5h |
| | 5.3 | App Lifecycle | Add `LifecycleEventObserver` to pause/resume BGM on `ON_PAUSE`/`ON_RESUME`. | 0.5h |
| | 5.4 | `SettingsScreen` | Add UI toggle for both "Background Music" and "Sound Effects". | 0.5h |
| **P6: QA** | 6.1 | QA Console | Verify BGM loops, SFX plays without latency, and settings toggles work. | 0.5h |
| | | | **Total Allocated Execution Time** | **7.5h** |

---
**CTO Approval Signature**: `[TN - CTO]`  
**Next Action**: Developer to begin Phase 1, Task 1.1 (Resource setup & SFX assets).