import com.google.devtools.ksp.gradle.KspExtension
import com.luigi.lifeos.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention for any module that defines Room entities/DAOs/databases.
 * Adds runtime + compiler deps and tells KSP where to emit Room schemas.
 *
 * Schemas live in `<module>/schemas/` and SHOULD be checked into git so
 * Room can auto-validate migrations against the prior schema.
 */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")

        extensions.configure<KspExtension> {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.generateKotlin", "true")
        }

        dependencies {
            add("implementation", libs.findLibrary("room-runtime").get())
            add("implementation", libs.findLibrary("room-ktx").get())
            add("ksp", libs.findLibrary("room-compiler").get())
        }
    }
}
