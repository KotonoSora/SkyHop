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

# Game + shared module models (Prevent field obfuscation for data integrity)
-keep class local.flappybird.engine.** { *; }
-keep class local.core.billing.** { *; }
-keep class local.core.shop.coins.** { *; }
-keep class local.core.ui.** { *; }
-keep class local.flappybird.shop.** { *; }
-keep class local.flappybird.ui.** { *; }

# Support for Previews in release if needed (rare but useful for debugging)
-keep class * implements androidx.compose.ui.tooling.preview.Preview

# Android System / Standard Library
-dontwarn android.util.Half
-dontwarn java.lang.invoke.*
-dontwarn sun.misc.Unsafe
