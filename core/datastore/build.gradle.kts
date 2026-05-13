plugins {
    alias(libs.plugins.lifeos.android.library)
    alias(libs.plugins.lifeos.android.hilt)
}

android {
    namespace = "com.luigi.lifeos.core.datastore"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
}
