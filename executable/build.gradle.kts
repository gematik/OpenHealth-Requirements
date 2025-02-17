plugins {
    application
    alias(libs.plugins.kotlinMultiplatform) apply false
}

application {
    mainClass.set("de.gematik.openhealth.reguirements.Main.kt")
}

dependencies {
    implementation(project(":shared"))
}
