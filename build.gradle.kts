plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false

    alias(libs.plugins.detekt) apply true
}

group = "de.gematik.openhealth.requirements"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

//tasks.test {
//    useJUnitPlatform()
//}
//kotlin {
//    jvmToolchain(17)
//}