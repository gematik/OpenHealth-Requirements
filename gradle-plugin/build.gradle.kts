/*
 * Copyright 2025 gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.mavenPublish)
    `java-gradle-plugin`
}

group = project.findProperty("gematik.baseGroup") as String
version = project.findProperty("gematik.version") as String

gradlePlugin {
    website = "https://github.com/gematik/OpenHealth-Requirements"
    vcsUrl = "https://github.com/gematik/OpenHealth-Requirements.git"
    plugins {
        create("requirementExtractorPlugin") {
            id = "de.gematik.openhealth.requirements"
            implementationClass = "de.gematik.openhealth.requirements.plugin.RequirementExtractorPlugin"
            displayName = "OpenHealth Requirement Extractor Plugin"
            description = "A plugin to extract and manage requirements"
            tags = listOf("requirements", "extractor", "parser", "gematik", "openhealth")
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // signAllPublications()

    coordinates(group.toString(), "requirements", version.toString())

    pom {
        name = "OpenHealth Requirements Plugin"
        description = "A plugin to extract and manage requirements"
        inceptionYear = "2025"
        url = "https://github.com/gematik/OpenHealth-Requirements"
        licenses {
            license {
                name = "Apache 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                name = "gematik GmbH"
                url = "https://github.com/gematik"
            }
        }
    }
}

dependencies {
    implementation(project(":core"))
}
