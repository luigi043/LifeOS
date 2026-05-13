plugins {
    alias(libs.plugins.lifeos.android.application)
    alias(libs.plugins.lifeos.android.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.luigi.lifeos"

    defaultConfig {
        applicationId = "com.luigi.lifeos"
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))

    // Features — :app wires every screen into the nav graph
    implementation(project(":feature:today"))
    implementation(project(":feature:finance"))
    implementation(project(":feature:workout"))
    implementation(project(":feature:nutrition"))
    implementation(project(":feature:grocery"))
    implementation(project(":feature:mentalhealth"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:habits"))
    implementation(project(":feature:analytics"))
    implementation(project(":feature:settings"))

    // Cross-cutting
    implementation(project(":sync"))
    implementation(project(":notifications"))
    implementation(project(":ai"))

    // Compose (BOM-managed via convention plugin, but app needs to declare them directly)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Foundation
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)

    // Background work + Hilt-Work bridge for @HiltWorker
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
}
