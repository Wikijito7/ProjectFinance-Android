buildscript {
    repositories {
        google()  // Repositorio de Maven de Google
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.9")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
    }
}

plugins {
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    // Utiliza ksp en lugar de kapt
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
