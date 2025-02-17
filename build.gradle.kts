plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

group = "de.gematik.openhealth.requirements"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}