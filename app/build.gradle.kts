plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.Iotaii.car_tracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.Iotaii.car_tracker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.osmdroid.android)
    implementation(libs.datastore.preferences)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.legacy.support.v4)
    val lifecycle_version = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation (libs.recyclerview)
    implementation (libs.material.v140)
    implementation (libs.glide)
    implementation (libs.android.sdk)
    implementation (libs.android.sdk.v1002)
    implementation (libs.okhttp)
    annotationProcessor (libs.compiler)
    implementation (libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}