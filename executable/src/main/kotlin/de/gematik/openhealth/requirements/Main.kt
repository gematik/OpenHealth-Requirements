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

package de.gematik.openhealth.requirements

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

class Main : CliktCommand() {
    private val commentPrefix by option(help = "Prefix for comments").default("//")
    private val outputFile by option(help = "Output file").file().default(File("requirements.csv"))
    private val files by argument(help = "File contents and paths in the format <content>:<path>").multiple()

    override fun run() {
        if (files.isEmpty()) {
            return
        }

        val parsedFiles =
            files
                .map {
                    val parts = it.split(":", limit = 2)
                    if (parts.size != 2) {
                        println("Error: Invalid file argument format! Expected <content>:<path>, but got '$it'")
                        return
                    }
                    parts[0] to parts[1]
                }.asSequence()

        val extractor = RequirementExtractor()
        val requirements = extractor.extractRequirements(parsedFiles, commentPrefix)

        println("Extracted ${requirements.size} requirements. Saving to ${outputFile.path}...")

        extractor.writeToCsv(requirements, outputFile.path)

        println("📁 Results saved in: ${outputFile.path}")
    }
}

fun main(args: Array<String>) = Main().main(args)
