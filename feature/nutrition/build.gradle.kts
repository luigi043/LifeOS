plugins {
    alias(libs.plugins.lifeos.android.feature)
}

android {
    namespace = "com.luigi.lifeos.feature.nutrition"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}
