// Top-level build file where you can add configuration options common to all sub-projects/modules.
val appPackageName by extra { "com.example.shikiflow" }
val graphQLPackageName by extra { "com.example.graphql" }

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    alias(libs.plugins.compose.compiler) apply false
}