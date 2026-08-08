plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.probasketacademy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.probasketacademy"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.coroutines.play.services)

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Hilt (Corregido: solo un procesador KSP para evitar conflictos)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines & Serialization
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // Lifecycle & Navigation
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.navigation.compose)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.dagger.hilt.android.testing)
    testImplementation(libs.androidx.room.testing)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.dagger.hilt.android.testing)

    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}