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

-dontobfuscate

# Сохраняем типы для рефлексии
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*
-keepattributes SourceFile, LineNumberTable

# 2. Правила для Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# 3. Правила для Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * implements com.google.gson.TypeAdapterFactory
-keepclassmembers class * implements com.google.gson.JsonSerializer
-keepclassmembers class * implements com.google.gson.JsonDeserializer

#Kotlin-сериализацию или @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}