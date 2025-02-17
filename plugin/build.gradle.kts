plugins {
    //alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("requirementExtractorPlugin") {
            id = "de.gematik.openhealth.requirements.plugin"
            implementationClass = "de.gematik.openhealth.requirements.plugin.RequirementExtractorPlugin"
        }
    }
}

dependencies {
    implementation(project(":shared"))
}