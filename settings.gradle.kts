pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // MPAndroidChart
    }
}

rootProject.name = "LuigiLifeOS"

include(":app")

// Core modules — building blocks shared across features
include(":core:common")
include(":core:designsystem")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:datastore")

// Feature modules — one per major domain
include(":feature:today")
include(":feature:finance")
include(":feature:workout")
include(":feature:nutrition")
include(":feature:grocery")
include(":feature:mentalhealth")
include(":feature:profile")
include(":feature:habits")
include(":feature:analytics")
include(":feature:settings")

// Cross-cutting modules
include(":sync")
include(":notifications")
include(":ai")
