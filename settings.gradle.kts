pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    val kotlinVersion = extra["kotlin.version"] as String

    plugins {
        kotlin("multiplatform").version(kotlinVersion)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        kotlin("plugin.serialization") version kotlinVersion
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlinx.coroutines", "1.7.0-RC")
            version("kotlinx.serialization", "1.5.0")

            library("kotlinx.serialization.json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("kotlinx.serialization")

            library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinx.coroutines")

        }
    }
}

rootProject.name = "housingcalc"

