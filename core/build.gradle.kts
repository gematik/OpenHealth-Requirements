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
}

group = project.findProperty("gematik.baseGroup") as String
version = project.findProperty("gematik.version") as String

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(libs.doyaaaaaken.csv)
            }
        }
        val test by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
