# SkyHop ProGuard Rules

# -----------------------------------------------------------------------------------
# General Rules
# -----------------------------------------------------------------------------------

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Preserve Annotations and Signatures for Retrofit, Room, and Moshi
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# -----------------------------------------------------------------------------------
# Jetpack Compose
# -----------------------------------------------------------------------------------
# Compose rules are generally included in the library, but keeping some common ones.
-keepclassmembers class androidx.compose.ui.platform.ComposeView {
   public *;
}

# -----------------------------------------------------------------------------------
# Room
# -----------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# -----------------------------------------------------------------------------------
# Retrofit / OkHttp
# -----------------------------------------------------------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# -----------------------------------------------------------------------------------
# Moshi (for JSON parsing)
# -----------------------------------------------------------------------------------
# Keep classes used with Moshi for serialization
-keep class com.kotonosora.skyboundhopper.model.** { *; }

# -----------------------------------------------------------------------------------
# Coroutines
# -----------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# -----------------------------------------------------------------------------------
# Google Play Billing
# -----------------------------------------------------------------------------------
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# -----------------------------------------------------------------------------------
# Coil (Image Loading)
# -----------------------------------------------------------------------------------
-dontwarn coil.**

# -----------------------------------------------------------------------------------
# SkyHop Models
# -----------------------------------------------------------------------------------
# Ensure your data models are not obfuscated to avoid issues with Room or Moshi
-keepclassmembers class com.kotonosora.skyboundhopper.model.** { *; }
