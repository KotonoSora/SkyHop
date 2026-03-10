# 🚀 SkyHop: Firebase Master Implementation Specification (v7.0)
**Role**: Technical Specification & Execution Roadmap  
**Status**: CTO Approved - Ready for Engineering

---

## 1. Executive Summary
This document serves as the single source of truth for integrating Firebase into SkyHop. It defines the architecture required to transition SkyHop into a data-driven application. 

The strategy relies on three core pillars:
1. **Telemetry & Funnel Tracking**: Comprehensive analytics for gameplay and monetization.
2. **Dynamic Configuration**: A highly scalable, remote-controlled virtual economy and game balancing system.
3. **Application Stability**: Real-time crash reporting and stack-trace de-obfuscation.

---

## 2. Architecture & Dependencies

### 2.1 SDK Stack (Firebase BoM)
To guarantee version compatibility, all Firebase libraries will be managed via the Firebase Bill of Materials (BoM).
*   `firebase-analytics`: Core telemetry and user properties.
*   `firebase-crashlytics`: Stability monitoring.
*   `firebase-config`: Cloud-based parameter management.

### 2.2 Pre-flight Checklist
| Status | Step | Action |
|:---:|:---|:---|
| [ ] | **2.2.1** | Place `google-services.json` in the `/app` root directory. |
| [ ] | **2.2.2** | Apply `com.google.gms.google-services` plugin in `app/build.gradle.kts`. |
| [ ] | **2.2.3** | Apply `com.google.firebase.crashlytics` plugin in `app/build.gradle.kts`. |

---

## 3. Telemetry Schema & Success Metrics

### 3.1 Gameplay Engagement (`GameViewModel` & `GameScreen`)
Based on the direct actions available in the `GameScreen.kt` UI flow.

| Event Name | Trigger Context | Key Parameters | Implementation Rules |
|---|---|---|---|
| `level_start` | Session initiation via `startGame` | `skin_id`, `level_name` | Extract `skin_id` directly from current UI state. |
| `game_over` | Player death/collision | `score`, `level`, `duration_sec` | Compute `duration_sec` immediately before state transition. |
| `game_abandoned` | User presses "Back/Home" during active run | `current_score`, `duration_sec` | Triggered in `onBackToHome` if `!gameState.isGameOver`. Measures rage-quits. |
| `click_play_again` | Tapping "Play Again" on Game Over Overlay | `previous_score`, `skin_id` | Triggered inside the `GameOverShopOverlay`'s `onPlayAgain` callback. |
| `click_return_home` | Tapping "Home" on Game Over Overlay | `final_score` | Triggered inside the `GameOverShopOverlay`'s `onHome` callback. |
| `power_up_used` | Item activation | `item_id`, `remaining_qty` | Must be logged *before* the item modifies game physics/state. |

### 3.2 Monetization Funnel (`CoinStoreScreen` & Billing)

Tracks the user journey from shop entry to successful payment.

| Step | Event Name | Key Parameters | Trigger Context |
|---|---|---|---|
| **1. Intent** | `click_get_coins` | `source_screen` | User taps "+" or "Get Coins" on any screen. |
| **2. Browse** | `screen_view` | `firebase_screen` | Entry into the `CoinStoreScreen`. |
| **3. Select** | `select_coin_pack` | `pack_id`, `price` | User selects a specific coin package. |
| **4. Checkout** | `iap_initiated` | `product_id` | System invokes Google Play Billing dialog. |
| **5. Success** | `in_app_purchase` | `product_id`, `value`, `currency` | Play Store returns a successful transaction state. |
| **6. Drop-off** | `iap_abandoned` | `product_id`, `reason` | User dismisses/cancels the Play Store dialog. |

### 3.3 Core KPIs
*   **Store Conversion (Pull-through)**: `in_app_purchase` / `iap_store_open`. *(Target: > 10%)*
*   **Checkout Abandonment**: `iap_abandoned` / `iap_initiated`. *(Target: < 15%)*
*   **Economy Balance**: Total `earn_virtual_currency` vs. Total `spend_virtual_currency`.

---

## 4. Remote Configuration & Dynamic UI

### 4.1 Live Game Balancing
Fallback defaults must be hardcoded in case of network failure.
*   **`gravity`** (Float): Vertical acceleration modifier.
*   **`pipe_speed`** (Float): Horizontal obstacle velocity.

### 4.2 Scalable Shop Inventory
To support infinite store expansion (e.g., adding a `coins_5000` package) without requiring a Play Store app update, the shop inventory is driven by a JSON array.

*   **Remote Config Key**: `iap_inventory_list`
*   **JSON Structure**:
    ```json
    [
      { "sku_id": "coins_100", "coin_amount": 100, "sort_order": 1 },
      { "sku_id": "coins_500", "coin_amount": 500, "sort_order": 2 },
      { "sku_id": "coins_1000", "coin_amount": 1000, "sort_order": 3 },
      { "sku_id": "coins_5000", "coin_amount": 5000, "sort_order": 4 }
    ]
    ```

### 4.3 Dynamic UI Rendering (Jetpack Compose)
1.  **State Management**: `ConfigManager` parses the JSON into `List<CoinPackConfig>` and passes it to `ShopViewModel`.
2.  **Billing Layer**: `BillingClient` maps the `sku_id` properties to fetch localized real-money prices.
3.  **View Layer**: `CoinStoreScreen` iterates through the combined state to render buttons dynamically.
    ```kotlin
    @Composable
    fun CoinStoreScreen(viewModel: ShopViewModel) {
        val dynamicStoreItems by viewModel.storeItems.collectAsState() 
        Column {
            dynamicStoreItems.forEach { item ->
                CoinStoreButton(
                    coinAmount = item.config.coin_amount,
                    localizedPrice = item.playStorePrice,
                    onClick = { viewModel.initiatePurchase(item.config.sku_id) }
                )
            }
        }
    }
    ```

---

## 5. Micro-Task Execution Roadmap (Max 0.5h/Task)

| Phase | Task ID | Target Component | Task Description | Est. Time |
|---|---|---|---|---|
| **P1: Init** | 1.1 | Gradle / Root | Add `google-services.json` and configure `build.gradle.kts` (BoM, Plugins). | 0.5h |
| | 1.2 | `AnalyticsManager` | Create Singleton structure; Add basic app initialization code. | 0.5h |
| | 1.3 | `AnalyticsManager` | Implement `logGameStart` and `logGameOver` functions with custom parameters. | 0.5h |
| | 1.4 | `AnalyticsManager` | Implement Monetization Funnel logging functions (`logIAPInitiated`, etc.). | 0.5h |
| **P2: Config** | 2.1 | `ConfigManager` | Create class; Implement `Firebase.remoteConfig` initialization with default values. | 0.5h |
| | 2.2 | Data Model | Create `CoinPackConfig` data class for JSON parsing (e.g., Moshi/Serialization). | 0.5h |
| | 2.3 | `ConfigManager` | Implement logic to fetch, parse, and expose `iap_inventory_list` JSON array. | 0.5h |
| **P3: Tracking**| 3.1 | `MainActivity` | Implement `screen_view` triggers for navigation state changes. | 0.5h |
| | 3.2 | `GameViewModel` | Inject `AnalyticsManager`; trigger `logGameStart` upon new run. | 0.5h |
| | 3.3 | `GameViewModel` | Calculate `duration_sec` and trigger `logGameOver` upon death collision. | 0.5h |
| | 3.4 | `GameViewModel` | Trigger `power_up_used` analytics when an item is activated. | 0.5h |
| | 3.5 | `GameScreen` | Implement `game_abandoned`, `click_play_again`, and `click_return_home` via Compose callbacks. | 0.5h |
| **P4: Shop UI** | 4.1 | `ShopViewModel` | Connect `ConfigManager` state flow; Extract dynamic `sku_id` list. | 0.5h |
| | 4.2 | Billing Layer | Modify Google Play query logic to accept dynamic SKU list instead of hardcoded list. | 0.5h |
| | 4.3 | `ShopViewModel` | Combine Config metadata with localized Play Store pricing data into a unified StateFlow. | 0.5h |
| | 4.4 | `CoinStoreScreen` | Replace hardcoded buttons with dynamic iteration (`forEach`) over the ViewModel state. | 0.5h |
| **P5: IAP Log** | 5.1 | `CoinStoreScreen` | Add `click_get_coins` intent tracking across the app UI (e.g., Home, Shop headers). | 0.5h |
| | 5.2 | `CoinStoreScreen` | Add `select_coin_pack` and `iap_initiated` to the dynamic buy buttons. | 0.5h |
| | 5.3 | Billing Logic | Insert standard `in_app_purchase` event inside successful transaction callback. | 0.5h |
| | 5.4 | Billing Logic | Insert `iap_abandoned` tracking inside canceled transaction callback. | 0.5h |
| **P6: QA** | 6.1 | Crashlytics | Force a RuntimeException crash; Verify stack trace appears in console. | 0.5h |
| | 6.2 | QA Console | Test full IAP funnel on device while monitoring **Firebase DebugView**. | 0.5h |
| | | | **Total Micro-Task Allocation** | **11.0h** |

---
**CTO Approval Signature**: `[TN - CTO]`  
**Next Action**: Developer to begin Phase 1, Task 1.1.
