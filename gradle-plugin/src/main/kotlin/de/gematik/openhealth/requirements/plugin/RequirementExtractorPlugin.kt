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

package de.gematik.openhealth.requirements.plugin

import de.gematik.openhealth.requirements.FileContent
import de.gematik.openhealth.requirements.RequirementExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import org.gradle.api.tasks.InputFiles

class RequirementExtractorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("extractRequirements", ExtractRequirementsTask::class.java)
    }
}

abstract class ExtractRequirementsTask : DefaultTask() {
    @InputFiles
    @Optional
    var filesToScan: ConfigurableFileTree = project.fileTree(".")

    @Input
    @Optional
    var commentPrefix: String = project.findProperty("requirementCommentPrefix") as? String ?: "//"

    @Input
    @Optional
    var outputFile: String = project.findProperty("requirementOutputFile") as? String ?: "requirements.csv"

    private val rootPath: File = project.rootDir

    @TaskAction
    fun run() {
        val extractor = RequirementExtractor()

        val files =
            filesToScan
                .map { file ->
                    FileContent(path = file.relativeTo(rootPath).path, content = file.readText())
                }.asSequence()

        val requirements = extractor.extractRequirements(files, commentPrefix)
        extractor.writeToCsv(requirements, outputFile)

        println("✅ Extracted ${requirements.size} requirements. Results saved in: $outputFile")
    }
}
