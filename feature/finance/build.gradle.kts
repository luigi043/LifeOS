plugins {
    alias(libs.plugins.lifeos.android.feature)
}

android {
    namespace = "com.luigi.lifeos.feature.finance"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
