rootProject.name = "de.gematik.openhealth.requirements"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // includeBuild("requirement-extractor-plugin")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":shared", ":plugin", ":executable")
