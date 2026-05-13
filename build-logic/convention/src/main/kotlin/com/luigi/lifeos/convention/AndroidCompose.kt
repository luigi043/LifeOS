package com.luigi.lifeos.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.dependencies

/**
 * Shared Compose configuration. Applied by `lifeos.android.library.compose`
 * and `lifeos.android.application` so every module reaches for the same
 * Compose BOM + tooling.
 */
internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    val libs = libs
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        listOf(
            "androidx-compose-ui",
            "androidx-compose-ui-graphics",
            "androidx-compose-ui-tooling-preview",
            "androidx-compose-material3",
            "androidx-compose-material-icons-extended",
            "androidx-compose-foundation",
            "androidx-activity-compose",
            "androidx-lifecycle-runtime-compose",
            "androidx-lifecycle-viewmodel-compose",
        ).forEach { alias ->
            add("implementation", libs.findLibrary(alias).get())
        }

        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
        add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test").get())
    }
}
