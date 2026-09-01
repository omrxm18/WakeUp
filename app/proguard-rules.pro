# Keep the AccessibilityService — referenced only via manifest string, R8 can't see that link
-keep class com.omrxm18.wakeup.util.CaffeineService { *; }

# Keep the Application subclass — instantiated by the OS via manifest, same issue
-keep class com.omrxm18.wakeup.App { *; }

# Keep Activities referenced from the manifest (standard Android practice,
# but explicit keep avoids surprises since AGP's default rules can miss custom ones)
-keep class com.omrxm18.wakeup.MainActivity { *; }

# ViewBinding-generated classes — don't obfuscate/strip generated binding classes
-keep class com.omrxm18.wakeup.databinding.** { *; }

# Standard Kotlin metadata / reflection safety (helps if anything uses reflection
# or Kotlin's Metadata annotation, e.g. some libraries introspect data classes)
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-dontwarn kotlin.**

# Keep default no-arg constructors for classes instantiated via reflection
# (not strictly needed yet, but common gotcha if you add libraries like Gson/Moshi later)
-keepclassmembers class * {
    public <init>();
}
