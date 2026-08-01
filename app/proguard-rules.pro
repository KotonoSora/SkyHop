# Project General
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, SourceFile, LineNumberTable

# Jetpack Compose
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView {
    void *;
}
-keep class androidx.compose.runtime.Recomposer { *; }
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
-dontwarn androidx.compose.ui.platform.AndroidComposeView

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModelProvider$Factory

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# Google Play Billing (Mandatory for Store flow)
-dontwarn com.android.billingclient.api.**
-keep class com.android.vending.billing.**

# Coil (Image Loading)
-dontwarn coil.**

# DataStore (Persistence)
-keep class androidx.datastore.preferences.core.** { *; }
-dontwarn androidx.datastore.**

# Game Domain Models (Prevent field obfuscation for data integrity)
-keep class com.kotonosora.flappybird.domain.model.** { *; }
-keep class com.kotonosora.flappybird.data.** { *; }
-keep class com.kotonosora.flappybird.feature.shop.** { *; }

# Support for Previews in release if needed (rare but useful for debugging)
-keep class * implements androidx.compose.ui.tooling.preview.Preview

# Android System / Standard Library
-dontwarn android.util.Half
-dontwarn java.lang.invoke.*
-dontwarn sun.misc.Unsafe
