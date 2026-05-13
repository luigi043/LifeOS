plugins {
    alias(libs.plugins.lifeos.android.feature)
}

android {
    namespace = "com.luigi.lifeos.feature.grocery"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}
