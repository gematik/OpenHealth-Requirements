import com.vanniktech.maven.publish.SonatypeHost

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

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = project.findProperty("gematik.baseGroup") as String
version = project.findProperty("gematik.version") as String

dependencies {
    implementation(libs.doyaaaaaken.csv)
    testImplementation(libs.kotlin.test)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    coordinates(group.toString(), "requirements-core", version.toString())

    pom {
        name = "OpenHealth Requirements Core Library"
        description = "OpenHealth Requirements Core Library"
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
        scm {
            url = "https://github.com/gematik/OpenHealth-Requirements"
            connection = "scm:git:https://github.com/gematik/OpenHealth-Requirements.git"
            developerConnection = "scm:git:https://github.com/gematik/OpenHealth-Requirements.git"
        }
    }
}
