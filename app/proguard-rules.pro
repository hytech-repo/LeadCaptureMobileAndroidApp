# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# This tells R8 to rewrite class names inside strings like android:name in navigation XML
-keepattributes Signature
-adaptclassstrings

# Keep Fragment constructors for Navigation Component usage
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>();
    public <init>(android.content.Context);
}

# Preserve the constructor in your BaseFragment (which may be generic and reflection-sensitive)
-keepclassmembers class com.eva.lead.capture.ui.base.BaseFragment {
    public <init>(...);
}

## Keep ViewModel classes
-keep public class * extends androidx.lifecycle.ViewModel {
    public <init>(android.content.Context);
    public <init>();
}
-keep class com.eva.lead.capture.domain.model.** { *; }
-keep class com.eva.lead.capture.domain.model.entity.** { *; }

-keep class com.google.gson.reflect.TypeToken { *; }

-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}