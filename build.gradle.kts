plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

group = project.findProperty("gematik.baseGroup") as String
version = project.findProperty("gematik.version") as String

repositories {
    mavenCentral()
}

tasks.register("licenseHeaderCheck") {
    val licenseTextRegex = "Copyright \\d{4} gematik GmbH".toRegex()

    val filesWithoutLicense =
        fileTree(".") {
            include("**/src/**/*.kt", "**/src/**/*.kts")
            exclude("**.*/*", "**/build/**", "**/dist/**", "**/generated/**", "**/node_modules/**")
        }.files.filter { file ->
            !file.readText().contains(licenseTextRegex)
        }

    doLast {
        if (filesWithoutLicense.isNotEmpty()) {
            filesWithoutLicense.forEach { file ->
                println("file://${file.absolutePath}: Missing or incorrect license header")
            }
            throw GradleException(
                "License check failed. Missing headers in ${filesWithoutLicense.size} files.",
            )
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    source.from(
        files(
            fileTree(".") {
                include("**/src/**/*.kt")
                exclude("**/build/**", "**/generated/**")
            },
        ),
    )
}

val ktlint by configurations.creating

dependencies {
    ktlint(libs.ktlint.cli) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

val ktlintCheck by tasks.registering(JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}
