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

# 1. Полный запрет на удаление информации о типах (Generic Types)
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

# 2. Если вы используете Kotlin, добавьте это (обязательно для Metadata)
-keep class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# 3. Retrofit 2 требует сохранения этих классов для работы с ParameterizedType
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# 4. Gson: сохраняем типы и поля
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * extends com.google.gson.TypeAdapter

# 5. Сохранение интерфейсов API
-keep interface * {
    @retrofit2.http.* <methods>;
}

# 6. Жесткое сохранение ваших моделей (DTO)
# Если ваши модели лежат в разных папках, лучше временно разрешить всё,
# чтобы понять, в этом ли была проблема:
-keep class **.model.** { *; }
-keep class **.dto.** { *; }
-keep class **.data.** { *; }