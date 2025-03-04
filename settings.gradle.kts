rootProject.name = "de.gematik.openhealth.requirements"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":core", ":gradle-plugin", ":cli")
