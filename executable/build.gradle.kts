plugins {
    application
    alias(libs.plugins.kotlin.jvm)

}

application {
    mainClass.set("de.gematik.openhealth.reguirements.Main.kt")
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.kotlin.clikt)
}
