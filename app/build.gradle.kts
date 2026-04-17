@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.collections.listOf

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.apollo)
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.shikiflow"
    compileSdk = 36


    androidResources {
        localeFilters += listOf(
            "en",
            "ru",
            "uk"
        )
    }

    defaultConfig {
        applicationId = "com.example.shikiflow"
        minSdk = 26
        targetSdk = 36
        versionCode = 19
        versionName = "0.4.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("long", "VERSION_TIMESTAMP", "${System.currentTimeMillis()}")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
        optIn.add("kotlin.time.ExperimentalTime")
    }
}

apollo {
    val appPackageName: String by rootProject.extra
    val graphQLPackageName: String by rootProject.extra

    service("shikimori-api") {
        packageName.set("$graphQLPackageName.shikimori")
        schemaFile.set(file("src/main/graphql/shikimori/schema.graphql"))
        srcDir("src/main/graphql/shikimori/sources")
        generateKotlinModels.set(true)
    }
    service("anilist-api") {
        packageName.set("$graphQLPackageName.anilist")
        schemaFile.set(file("src/main/graphql/anilist/schema.graphql"))
        srcDir("src/main/graphql/anilist/sources")
        generateKotlinModels.set(true)
        mapScalar(
            "CountryCode",
            "$appPackageName.data.datasource.dto.anilist.CountryOfOriginDto",
            "$appPackageName.data.datasource.dto.anilist.CountryOfOriginDto.countryOfOriginAdapter"
        )
    }
}

ksp {
    //[OUTDATED] Set ksp.incremental.log = true in gradle.properties and now KSP2 works fine...
    //Enabled aggregating task for Hilt and there are no problems now...
    ksp.useKsp2 = true // PSI issue with KSP2 I don't know how to fix
}

hilt {
    enableAggregatingTask = true
}

secrets {
    propertiesFileName = "secret.properties"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //Custom Tabs
    implementation(libs.androidx.browser)

    //Apollo (GraphQL)
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)

    //Paging3
    implementation(libs.androidx.paging.compose)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    //Ksoup — HTML Parser
    implementation(libs.ksoup)
    implementation(libs.ksoup.network)

    //Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3.android)

    //Coroutines
    implementation(libs.kotlinx.coroutines)

    //Zoomable
    implementation(libs.zoomable)

    //Exoplayer
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    //implementation(libs.androidx.media3.media.session)

    //Pager Indicator
    implementation(libs.pager.indicator)

    //Material3 Adaptive
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.navigation)
    implementation(libs.androidx.material3.navigation.suite)

    //Vico (Chart Library)
    //implementation(libs.vico.compose.m3)

    //Splash Screen
    implementation(libs.androidx.core.splash.screen)

    //Material Kolor
    implementation(libs.material.kolor)

    //AppCompat
    implementation(libs.androidx.appcompat)

    //Work Manager
    //implementation(libs.androidx.work.runtime.ktx)
    //implementation(libs.androidx.hilt.work)
    //ksp(libs.androidx.hilt.compiler)
}