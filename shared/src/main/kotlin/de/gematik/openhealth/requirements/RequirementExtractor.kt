package de.gematik.openhealth.requirements

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

data class Requirement(
    val reqId: String,
    val spec: String?,
    val desc: String,
    val filePath: String,
    val startLine: Int?,
    val endLine: Int?
)

class RequirementExtractor {
    private val startTag = "REQ-BEGIN"
    private val endTag = "REQ-END"
    private var prefix = "//"

    fun extractRequirements(fileParameter: Sequence<Pair<String, String>>, commentPrefix: String): List<Requirement> {
        prefix = commentPrefix.trim()
        val requirements = mutableListOf<Requirement>()
        val requirementsWithStart = mutableMapOf<String, Int>()
        val requirementsWithEnd = mutableMapOf<String, Int>()
        val requirementsWithSpec = mutableMapOf<String, String>()
        val requirementsWithDesc = mutableMapOf<String, String>()

        fileParameter.forEach { (content, path) ->
            val lines = content.lines()
            val beginLineIndices = mutableListOf<Int>()
            val endLineIndices = mutableListOf<Int>()

            for ((index, line) in lines.withIndex()) {
                if (line.contains(startTag)) {
                    beginLineIndices.add(index)
                }
                if (line.contains(endTag)) {
                    endLineIndices.add(index)
                }
            }

            beginLineIndices.forEach { beginLineIndex ->
                requirementsWithStart.putAll(mapStartPositions(lines, beginLineIndex))
                requirementsWithSpec.putAll(mapRequirementSpecifications(lines, beginLineIndex))
                requirementsWithDesc.putAll(mapRequirementDescriptions(lines, beginLineIndex))
            }

            endLineIndices.forEach { endLineIndex ->
                requirementsWithEnd.putAll(mapEndPositions(lines, endLineIndex))
            }

            requirementsWithStart.forEach { (requirement, startLine) ->
                val spec = requirementsWithSpec[requirement]
                val desc = requirementsWithDesc[requirement] ?: ""
                val endLine = requirementsWithEnd[requirement]

                if (spec != null && endLine != null) {
                    requirements.add(Requirement(requirement, spec, desc, path, startLine, endLine))
                }
            }
        }
        return requirements
    }

    private fun mapStartPositions(
        lines: List<String>,
        beginLineIndex: Int
    ): Map<String, Int> {
        val requirementsWithStart = mutableMapOf<String, Int>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()
        var firstNonCommentLineIndex: Int? = null

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()

            if (line.isCommentLine() && !line.hasControlPrefix()) {
                requirementsInFile.addAll(line.removeCommentPrefix().trim().mapCommaSeparatedValues())
            } else if (line.isNotEmpty() && !line.isCommentLine()) {
                firstNonCommentLineIndex = i
                break
            }
        }

        requirementsInFile.associateWithTo(requirementsWithStart) {
            firstNonCommentLineIndex ?: beginLineIndex
        }
        return requirementsWithStart.toMap()
    }

    private fun mapEndPositions(
        lines: List<String>,
        endLineIndex: Int
    ): Map<String, Int> {
        val requirementsWithEnd = mutableMapOf<String, Int>()
        var lastNonCommentLineIndex: Int? = null

        for (i in endLineIndex - 1 downTo 0) {
            val line = lines[i].trim()

            if (line.isNotEmpty() && !line.isCommentLine()) {
                lastNonCommentLineIndex = i
                break
            }
        }

        val requirementKeys = lines[endLineIndex].splitByColonAndMapCommaSeparatedValues()
        requirementKeys.associateWithTo(requirementsWithEnd) {
            lastNonCommentLineIndex ?: endLineIndex
        }
        return requirementsWithEnd.toMap()
    }

    private fun mapRequirementSpecifications(
        lines: List<String>,
        beginLineIndex: Int
    ): Map<String, String> {
        val requirementsWithSpec = mutableMapOf<String, String>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()
            extractAdditionalRequirements(line, requirementsInFile)
            if (line.isCommentLine() && line.hasControlPrefix()) {
                val specification = line.removeCommentAndControlPrefix()
                requirementsInFile.forEach { requirement ->
                    requirementsWithSpec[requirement] = specification
                }
                break
            }
        }
        return requirementsWithSpec.toMap()
    }

    private fun mapRequirementDescriptions(
        lines: List<String>,
        beginLineIndex: Int
    ): Map<String, String> {
        val requirementsWithDesc = mutableMapOf<String, String>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()

        var descriptionStarted = false
        val descriptionBuilder = StringBuilder()

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()

            extractAdditionalRequirements(line, requirementsInFile)

            if (line.isCommentLineWithControlPrefix() && !descriptionStarted) {
                descriptionStarted = true
                continue
            } else if (line.isCommentLineWithControlPrefix()) {
                descriptionBuilder.append(line.removeCommentAndControlPrefix().trim())
            } else if (descriptionStarted && line.isCommentLine()) {
                descriptionBuilder.append(" ").append(line.removeCommentPrefix().trim())
            } else if (descriptionStarted && (line.isEmpty() || !line.isCommentLine())) {
                break
            }
        }

        val finalDescription = descriptionBuilder.toString().trim()
        if (finalDescription.isNotEmpty()) {
            requirementsInFile.forEach { requirement ->
                requirementsWithDesc[requirement] = finalDescription
            }
        }
        return requirementsWithDesc.toMap()
    }

    private fun extractAdditionalRequirements(line: String, requirementsInFile: MutableList<String>) {
        if (line.isCommentLine() && !line.hasControlPrefix()) {
            requirementsInFile.addAll(line.removeCommentPrefix().trim().mapCommaSeparatedValues())
        }
    }

    private fun String.isCommentLine(): Boolean {
        val trimmedLine = this.trim()
        return trimmedLine.startsWith(prefix)
    }

    private fun String.isCommentLineWithControlPrefix(): Boolean {
        val trimmedLine = this.trim()
        return trimmedLine.isCommentLine() && trimmedLine.contains("|")
    }

    private fun String.hasControlPrefix(): Boolean {
        return this.contains("|")
    }

    private fun String.splitByColonAndMapCommaSeparatedValues(): List<String> {
        return this.splitByColon().mapCommaSeparatedValues()
    }

    private fun String.mapCommaSeparatedValues(): List<String> {
        return this.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun String.splitByColon(): String {
        return this.split(":")[1].trim()
    }

    private fun String.removeCommentAndControlPrefix() : String {
        return this.removeCommentPrefix().trim().removePrefix("|").trim()
    }

    private fun String.removeCommentPrefix(): String {
        val trimmed = this.trim()
            if (trimmed.startsWith(prefix)) {
                return trimmed.removePrefix(prefix).trim()
            }
        return this
    }

    fun writeToCsv(requirements: List<Requirement>, outputFilePath: String) {
        csvWriter().open(outputFilePath) {
            writeRow(listOf("Requirement ID", "Specification", "Description", "File Path", "Start Line", "End Line"))
            requirements.forEach {
                writeRow(listOf(it.reqId, it.spec, it.desc, it.filePath, it.startLine, it.endLine))
            }
        }
    }
}