import com.android.build.api.dsl.LibraryExtension
import com.luigi.lifeos.convention.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Layered on top of `lifeos.android.library` for modules that ship Compose UI
 * (core/designsystem and any feature module). Enables the Compose build feature
 * and wires the Compose BOM + tooling dependencies.
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<LibraryExtension> {
            configureCompose(this)
        }
    }
}
