package de.gematik.openhealth.requirements

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File


class RequirementExtractorCommand : CliktCommand() {
    private val commentPrefix by option(help = "Prefix for comments").default("//")
    private val outputFile by option(help = "Output file").file().default(File("requirements.csv"))
    private val files by argument(help = "File contents and paths in the format <content>:<path>").multiple()

    override fun run() {
        if (files.isEmpty()) {
            return
        }

        val parsedFiles = files.map {
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

fun main(args: Array<String>) = RequirementExtractorCommand().main(args)