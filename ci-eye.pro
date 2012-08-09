-injars build/ci-eye-1.0.0-SNAPSHOT-20120807090858.jar
-outjars build/ci-eye-dist.jar
-libraryjars  <java.home>/lib/rt.jar
-libraryjars vendor/buildlib/jsr305-2.0.0.jar

-dontoptimize
-dontobfuscate
-dontwarn sun.misc.Unsafe
-dontwarn org.apache.commons.logging.impl.*
-dontwarn org.apache.http.impl.auth.*

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}
-keep public class * extends org.netmelody.cieye.core.observation.CiSpy
-keep class org.apache.commons.logging.*
-keep class org.apache.commons.logging.impl.*
