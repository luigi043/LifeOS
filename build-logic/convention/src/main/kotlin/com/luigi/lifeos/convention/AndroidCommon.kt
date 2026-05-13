package com.luigi.lifeos.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

/**
 * Targets shared by every Android module. Centralised so a stack-wide bump
 * (e.g. compileSdk 35 → 36) is a one-line change.
 */
internal const val LIFEOS_COMPILE_SDK = 35
internal const val LIFEOS_MIN_SDK = 26
internal const val LIFEOS_TARGET_SDK = 35

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Apply Android baseline config: SDK levels, Java 17, Kotlin JVM target.
 * Called by both application and library convention plugins.
 */
internal fun Project.configureAndroidBase(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = LIFEOS_COMPILE_SDK

        defaultConfig {
            minSdk = LIFEOS_MIN_SDK
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    extensions.configure(KotlinAndroidProjectExtension::class.java) {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }
}
