# CalcMate Proguard / R8 Optimization Rules

# Google Play Services & AdMob Rules
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
-keep class com.google.android.gms.ads.nativead.** { *; }

# Keep AdMob WebViews and JavaScript interfaces
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep DataStore classes
-keepclassmembers class * extends androidx.datastore.preferences.core.Preferences { *; }
