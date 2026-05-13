import com.android.build.api.dsl.LibraryExtension
import com.luigi.lifeos.convention.LIFEOS_TARGET_SDK
import com.luigi.lifeos.convention.configureAndroidBase
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Default Android library setup. Applied by every non-Compose Android module
 * (core/data, core/network, core/database, etc.).
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("com.google.devtools.ksp")
        }

        extensions.configure<LibraryExtension> {
            configureAndroidBase(this)
            defaultConfig.apply {
                @Suppress("UnstableApiUsage")
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            // Each module supplies its own targetSdk for instrumented tests
            defaultConfig.targetSdk = LIFEOS_TARGET_SDK
        }
    }
}
