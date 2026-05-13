import com.android.build.api.dsl.ApplicationExtension
import com.luigi.lifeos.convention.LIFEOS_TARGET_SDK
import com.luigi.lifeos.convention.configureAndroidBase
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin applied by the :app module.
 * Sets up the Android Application + Kotlin + Compose Compiler + KSP stack
 * and pins targetSdk / Compose options.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("com.google.devtools.ksp")
        }

        extensions.configure<ApplicationExtension> {
            configureAndroidBase(this)
            defaultConfig.targetSdk = LIFEOS_TARGET_SDK
        }
    }
}
