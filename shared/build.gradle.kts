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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

//    signAllPublications()

    coordinates(group.toString(), "shared", version.toString())

    pom {
        name = "OpenHealth Requirements Plugin"
        description = "OpenHealth Requirements Plugin"
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
