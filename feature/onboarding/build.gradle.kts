plugins {
    alias(libs.plugins.lifeos.android.feature)
}

android {
    namespace = "com.luigi.lifeos.feature.onboarding"
}

dependencies {
    // Onboarding writes to the user profile (Room) and flips the
    // onboardingDone flag in DataStore — so it needs :core:data and
    // :core:datastore. No other feature dependencies.
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
