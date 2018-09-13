# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\DevanshuBackup\devanshuandroidsdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepclassmembers class BridgeJavaClass.WebViewJavascriptBridge {
   public *;
}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keep class net.sourceforge.zbar.** { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn org.apache.commons.**

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn com.androidquery.auth.**