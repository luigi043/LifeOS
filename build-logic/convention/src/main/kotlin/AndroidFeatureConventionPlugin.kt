import com.luigi.lifeos.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Aggregate plugin for `:feature:*` modules. Applies the Compose library
 * convention + Hilt + the standard set of deps every feature needs
 * (core:designsystem, core:domain, navigation, lifecycle).
 *
 * Why: feature modules differ in their *code*, not their *build config* —
 * keeping that convergent here avoids drift across 10 module scripts.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("lifeos.android.library")
            apply("lifeos.android.library.compose")
            apply("lifeos.android.hilt")
        }

        dependencies {
            add("implementation", project(":core:designsystem"))
            add("implementation", project(":core:domain"))
            add("implementation", project(":core:common"))
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("hilt-navigation-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
        }
    }
}
