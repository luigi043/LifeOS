plugins {
    alias(libs.plugins.lifeos.android.feature)
}

android {
    namespace = "com.luigi.lifeos.feature.workout"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}
