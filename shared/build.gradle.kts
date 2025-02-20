
plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}
kotlin {

    sourceSets {
        val main by getting {
            dependencies {
                implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0") // CSV library
            }
        }
        val test by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "de.gematik.openhealth.requirements"
            artifactId = "shared"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
