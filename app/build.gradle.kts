plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.apollo)
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.shikiflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shikiflow"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    hilt {
        enableAggregatingTask = false
    }
    apollo {
        service("shikimori-api") {
            packageName.set("com.example.graphql")
            schemaFile.set(file("src/main/graphql/schema.graphql"))
            srcDir("src/main/graphql/sources")
//            mapScalar("ISO8601Date", "kotlinx.datetime.Instant")
//            mapScalar("ISO8601DateTime", "kotlinx.datetime.Instant")
//            mapScalar("ID", "kotlin.Long")
//            mapScalar("PositiveInt", "kotlin.Int")
//            mapScalar("AnimeKindString", "com.example.shikiflow.data.mapper.AnimeKind")
            generateKotlinModels.set(true)
        }
    }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation (libs.okhttp)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation (libs.retrofit.kotlinx.serialization)
    implementation (libs.androidx.datastore.preferences)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.kotlinx.datetime)
    implementation (libs.androidx.navigation.compose)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //ConstraintLayout
    implementation (libs.androidx.constraintlayout.compose)

    //Custom Tabs
    implementation (libs.androidx.browser)

    //Apollo (GraphQL)
    implementation(libs.apollo.runtime)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //Paging3
    implementation(libs.androidx.paging.compose)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
}