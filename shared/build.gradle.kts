plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0") // CSV library
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}