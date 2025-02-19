plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
    `maven-publish`

}

gradlePlugin {
    plugins {
        create("requirementExtractorPlugin") {
            id = "de.gematik.openhealth.requirements.plugin"
            implementationClass = "de.gematik.openhealth.requirements.plugin.RequirementExtractorPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "de.gematik.openhealth"
            artifactId = "requirement-extractor-plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(project(":shared"))
}

