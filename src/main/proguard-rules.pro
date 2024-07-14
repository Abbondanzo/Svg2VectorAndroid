-dontobfuscate
-keepattributes SourceFile, LineNumberTable

-allowaccessmodification

-keep class com.vector.svg2vectorandroid.Runner {
  public static void main(java.lang.String[]);
}

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }
