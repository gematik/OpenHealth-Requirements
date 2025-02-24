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

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

data class Requirement(
    val reqId: String,
    val spec: String?,
    val desc: String,
    val filePath: String,
    val startLine: Int?,
    val endLine: Int?,
)

class RequirementExtractor {
    private val startTag = "REQ-BEGIN"
    private val endTag = "REQ-END"
    private var prefix = "//"

    fun extractRequirements(
        fileParameter: Sequence<Pair<String, String>>,
        commentPrefix: String,
    ): List<Requirement> {
        prefix = commentPrefix.trim()
        val requirements = mutableListOf<Requirement>()

        fileParameter.forEach { (content, path) ->
            val requirementsWithStart = mutableMapOf<String, Int>()
            val requirementsWithEnd = mutableMapOf<String, Int>()
            val requirementsWithSpec = mutableMapOf<String, String>()
            val requirementsWithDesc = mutableMapOf<String, String>()

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
        beginLineIndex: Int,
    ): Map<String, Int> {
        val requirementsWithStart = mutableMapOf<String, Int>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()
        var firstNonCommentLineIndex: Int? = null

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()

            if (line.isCommentLine(prefix) && !line.hasControlPrefix()) {
                requirementsInFile.addAll(line.removeCommentPrefix(prefix).trim().mapCommaSeparatedValues())
            } else if (line.isNotEmpty() && !line.isCommentLine(prefix)) {
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
        endLineIndex: Int,
    ): Map<String, Int> {
        val requirementsWithEnd = mutableMapOf<String, Int>()
        var lastNonCommentLineIndex: Int? = null

        for (i in endLineIndex - 1 downTo 0) {
            val line = lines[i].trim()

            if (line.isNotEmpty() && !line.isCommentLine(prefix)) {
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
        beginLineIndex: Int,
    ): Map<String, String> {
        val requirementsWithSpec = mutableMapOf<String, String>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()
            extractAdditionalRequirements(line, requirementsInFile, prefix)
            if (line.isCommentLine(prefix) && line.hasControlPrefix()) {
                val specification = line.removeCommentAndControlPrefix(prefix)
                requirementsInFile.forEach { requirement ->
                    requirementsWithSpec[requirement] = specification
                }
                break
            }
        }
        return requirementsWithSpec.toMap()
    }

    private fun extractAdditionalRequirements(
        line: String,
        requirementsInFile: MutableList<String>,
        prefix: String,
    ) {
        if (line.isCommentLine(prefix) && !line.hasControlPrefix()) {
            requirementsInFile.addAll(line.removeCommentPrefix(prefix).trim().mapCommaSeparatedValues())
        }
    }

    private fun mapRequirementDescriptions(
        lines: List<String>,
        beginLineIndex: Int,
    ): Map<String, String> {
        val requirementsWithDesc = mutableMapOf<String, String>()
        val requirementsInFile = lines[beginLineIndex].splitByColonAndMapCommaSeparatedValues().toMutableList()

        var descriptionStarted = false
        val descriptionBuilder = StringBuilder()

        for (i in beginLineIndex + 1 until lines.size) {
            val line = lines[i].trim()

            extractAdditionalRequirements(line, requirementsInFile, prefix)

            when {
                line.isCommentLineWithControlPrefix(prefix) && !descriptionStarted -> {
                    descriptionStarted = true
                }
                line.isCommentLineWithControlPrefix(prefix) -> {
                    descriptionBuilder.append(line.removeCommentAndControlPrefix(prefix).trim())
                }
                descriptionStarted && (line.isEmpty() || !line.isCommentLine(prefix) || line.contains(startTag)) -> {
                    break
                }
                descriptionStarted && line.isCommentLine(prefix) -> {
                    descriptionBuilder.append(" ").append(line.removeCommentPrefix(prefix).trim())
                }
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

    fun writeToCsv(
        requirements: List<Requirement>,
        outputFilePath: String,
    ) {
        csvWriter {
            delimiter = ';'
        }.open(outputFilePath) {
            writeRow(listOf("Requirement ID", "Specification", "Description", "File Path", "Start Line", "End Line"))
            requirements.forEach {
                writeRow(listOf(it.reqId, it.spec, it.desc, it.filePath, it.startLine, it.endLine))
            }
        }
    }
}
