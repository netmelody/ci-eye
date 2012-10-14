-injars build/ci-eye-in.jar
-outjars build/ci-eye-out.jar
-libraryjars  <java.home>/lib/rt.jar
-libraryjars  <java.home>/lib/jsse.jar
-libraryjars vendor/buildlib/jsr305-2.0.0.jar

-dontoptimize
-dontobfuscate
-dontwarn sun.misc.Unsafe
-dontwarn org.apache.commons.logging.impl.*
-dontwarn org.apache.http.impl.auth.*

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}
-keep public class * extends org.netmelody.cieye.core.observation.ObservationAgency
-keep public class * extends org.netmelody.cieye.core.observation.CiSpy
