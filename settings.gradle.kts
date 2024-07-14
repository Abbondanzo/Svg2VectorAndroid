pluginManagement {
    plugins {
        // This should match gradle/libs.versions.toml
        id("org.jetbrains.kotlin.jvm") version "1.9.22"
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Svg2VectorAndroid"
