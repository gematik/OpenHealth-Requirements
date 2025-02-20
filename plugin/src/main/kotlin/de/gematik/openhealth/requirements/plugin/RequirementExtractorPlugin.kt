package de.gematik.openhealth.requirements.plugin

import de.gematik.openhealth.requirements.RequirementExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

class RequirementExtractorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("extractRequirements", ExtractRequirementsTask::class.java)
    }
}

abstract class ExtractRequirementsTask : DefaultTask() {

    @Input
    @Optional
    var scanDirectory: String = project.findProperty("requirementScanDir") as? String ?: "."

    @Input
    @Optional
    var commentPrefix: String = project.findProperty("requirementCommentPrefix") as? String ?: "//"

    @Input
    @Optional
    var outputFile: String = project.findProperty("requirementOutputFile") as? String ?: "requirements.csv"

    private val rootPath: File = project.rootDir
    private val filesToScan: Set<File> = project.fileTree(scanDirectory).files

    @TaskAction
    fun run() {
        val extractor = RequirementExtractor()

        val files = filesToScan.map { file ->
            file.readText() to file.relativeTo(rootPath).path
        }.asSequence()

        val requirements = extractor.extractRequirements(files, commentPrefix)
        extractor.writeToCsv(requirements, outputFile)

        println("✅ Extracted ${requirements.size} requirements. Results saved in: $outputFile")
    }
}