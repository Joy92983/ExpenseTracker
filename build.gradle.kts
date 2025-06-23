// Top-level build.gradle file
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2") // or your version
        classpath("com.google.gms:google-services:4.3.15") // ✅ Required for Firebase
    }
}
