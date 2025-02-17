
plugins {
    alias(libs.plugins.kotlin.jvm)
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