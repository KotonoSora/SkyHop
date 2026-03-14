# SkyHop

**SkyHop** is a vibrant, fast-paced arcade game inspired by classic flight mechanics, reimagined with a modern **Material Design 3** aesthetic.

## ✨ Features

*   **Core Flight Mechanics**: Intuitive tap-to-jump gameplay with precise collision detection and smooth obstacle scrolling.
*   **Automated Start Sequence**: Every game begins with an **Auto Shield** mode followed by an **Auto Boost** (Auto Play) mode to ease players into the action.
*   **Coin-Based Economy**: Earn coins for every pipe passed. Use coins to purchase power-ups or unlock different skins.
*   **In-App Coin Packs**: Integrated **Google Play Billing** allowing users to purchase coin bundles (100, 500, or 1000 coins) with localized pricing.
*   **Diverse Skin Shop**: Unlock and select bird skins like **Space Voyager**, **Golden Phoenix**, and **Steam-Powered Flyer**.
*   **Game Over Purchase Flow**: If you crash, use coins to purchase an instant **Shield Revive** or **Boost Power-up**. If coins are low, you're seamlessly redirected to the Coin Store.
*   **Material 3 Aesthetic**: A visually striking interface using vibrant color schemes (Sky Blue, Amber), dynamic transitions, and full edge-to-edge display support.

## 🛠️ Technical Stack

*   **Kotlin**: Primary language for game logic and state management.
*   **Jetpack Compose**: Used for the entire UI, including the game canvas, HUD, and Material 3 screens.
*   **Google Play Billing Library**: Handles secure in-app purchases.
*   **Kotlin Coroutines & Flow**: Manages the game loop, timers, and reactive state updates.
*   **Jetpack DataStore**: Persistent storage for high scores, coin balance, and unlocked skins.
*   **Material 3**: Implementation of the latest Android design system for a modern, "Expressive" look.

## 🚀 Getting Started

1.  **Clone the repository**: `git clone <repo-url>`
2.  **Open in Android Studio**: Use the latest version.
3.  **Build and Run**: Select a device/emulator and click "Run".
4.  **Google Play Billing**: Ensure you have a valid `local.properties` or configuration if testing billing. Note: Testing on an emulator will use the Play Store's sandbox environment.

## 📸 Design Mockups
- Main Design: `/input_images/skyhop_ui_design.jpg`
- Shop & Power-ups: `/input_images/skyhop_expanded_shop_ui.jpg`
- Auto Play HUD: `/input_images/skyhop_autoplay_ui.jpg`
- Game Over Shop: `/input_images/skyhop_gameover_shop_ui.jpg`
- Coin Store: `/input_images/skyhop_coin_shop_ui.jpg`

---

## 📱 Google Play Store Assets

*   **App Name**: SkyHop: Vibrant Arcade Flyer
*   **App Icon**: 512 x 512 pixels (PNG or JPEG, max 1MB)
*   **Short Description**: Fly through obstacles, unlock different skins, and master the sky in this M3 arcade! (79 characters)
*   **Long Description**:
    Master the skies in SkyHop! Experience a high-energy, modern take on classic arcade flight mechanics reimagined with a vibrant Material Design 3 aesthetic.

    **Features:**
    * **Dynamic Gameplay**: Intuitive tap-to-jump mechanics. Dodge obstacles and survive as long as you can!
    * **Power-Up Start**: Every run begins with an automated Shield and Boost sequence to get you up to speed.
    * **Diverse Skin Shop**: Unlock unique birds like the Space Voyager, Golden Phoenix, and Steampunk Flyer.
    * **Gameplay Aides**: Use Energy Shields to survive hits and Score Boosters to increase your points.
    * **Coin Economy**: Earn coins by playing or grab coin packs from the store to unlock content.
    * **Material 3 Design**: A vibrant, expressive UI with smooth animations and full edge-to-edge display support.

    Join the flight today and see how you can hop!
*   **Screenshots**: 2-8 phone screenshots. Recommended resolution: 1242 x 2688 px (PNG or JPEG, max 8MB each, 16:9 or 9:16 aspect ratio).
*   **Target Age**: Everyone (PEGI 3 / ESRB E).

## ⚖️ Privacy Policy

*   **Information Collection and Use**: SkyHop collects minimal data required for functionality. This includes in-game progress (high scores, coin balance) stored locally on the device using Jetpack DataStore. No personally identifiable information (PII) is collected directly by the app.
*   **Third Party Access**: The app uses Google Play Billing Library for in-app purchases. Financial transactions are processed securely by Google. We do not have access to your credit card or payment details.
*   **Opt-Out Rights**: You can stop all collection of information by the Application easily by uninstalling the Application.
*   **Data Retention Policy**: Since data is stored locally, it is retained as long as the app is installed. Uninstalling the app will remove all local data.
*   **Children**: We do not use the Application to knowingly solicit data from or market to children under the age of 13.
*   **Security**: We provide physical, electronic, and procedural safeguards to protect information we process and maintain.
*   **Changes**: This Privacy Policy may be updated from time to time for any reason. We will notify you of any changes by posting the updated Privacy Policy here.
*   **Your Consent**: By using the Application, you are consenting to our processing of your information as set forth in this Privacy Policy.
*   **Contact Us**: For any questions, contact us at: reply@kotonosora.com
*   **Full Privacy Policy**: For more details, please visit our public privacy policy at [https://kotonosora.com/privacy](https://kotonosora.com/privacy).

---

Music by <a href="https://pixabay.com/users/the_mountain-3616498/?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=487020">Dmitrii Kolesnikov</a> from <a href="https://pixabay.com//?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=487020">Pixabay</a>

---

Developed as a robust, maintainable, and user-friendly Android application.

---

```
properties
# Add this to /SkyHop/local.properties
RELEASE_STORE_FILE=release-keystore.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
```

Then, place your release-keystore.jks in the `app/` folder. Since `local.properties` is ignored by Git, these values will stay only on your machine and won't be pushed to GitHub.
