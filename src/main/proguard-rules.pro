-dontobfuscate
-keepattributes SourceFile, LineNumberTable

#-allowaccessmodification
-printconfiguration config.txt

-keep class com.vector.svg2vectorandroid.Runner {
  public static void main(java.lang.String[]);
}
