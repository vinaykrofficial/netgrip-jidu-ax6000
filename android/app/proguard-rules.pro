# Keep Retrofit and Gson data models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.vinaykr.netgrip.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
