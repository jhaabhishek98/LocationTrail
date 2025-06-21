plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // Google Secrets Plugin (already aliased via `libs`)
        classpath(libs.secrets.gradle.plugin)

        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.27")


        // Navigation Safe Args Plugin
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}
