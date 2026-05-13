plugins {
    alias(libs.plugins.lifeos.android.library)
    alias(libs.plugins.lifeos.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.luigi.lifeos.core.network"

    buildFeatures {
        buildConfig = true // exposes BuildConfig.DEBUG to NetworkModule for logging
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}
