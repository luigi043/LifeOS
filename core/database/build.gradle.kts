plugins {
    alias(libs.plugins.lifeos.android.library)
    alias(libs.plugins.lifeos.android.hilt)
    alias(libs.plugins.lifeos.android.room)
}

android {
    namespace = "com.luigi.lifeos.core.database"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
}
