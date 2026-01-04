# قواعد ProGuard الأساسية
-keep class com.example.youtubedownloader.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.** { *; }

# للحفاظ على التوابع (annotations)
-keepattributes *Annotation*

# للحفاظ على البيانات (Data classes)
-keepclassmembers class * {
    *** get*();
    void set*(***);
}

# للحفاظ على أسماء الحقول
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# للحفاظ على الدوال الأصلية (native methods)
-keepclasseswithmembernames class * {
    native <methods>;
}

# للحفاظ على دوال الانعكاس (reflection)
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# مكتبة youtube-dl-android
-keep class com.github.yausername.youtubedl_android.** { *; }
-keep class com.github.yausername.youtubedl_android.library.** { *; }