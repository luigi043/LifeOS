import com.luigi.lifeos.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Wires Hilt into a module: applies the Hilt + KSP plugins and adds the
 * runtime + compiler deps. Apply this on any module that declares
 * `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, or `@Module`.
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.google.devtools.ksp")
            apply("com.google.dagger.hilt.android")
        }

        dependencies {
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())
        }
    }
}
