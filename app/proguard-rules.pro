# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\SAKSHAM SHAHA\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Jetpack Compose rules (standard Kotlin reflection / Compose optimizations)
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep ViewModel subclasses
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Immutable / Stable model classes
-keep class com.example.emicalculatorapp.model.** { *; }
