/*
 * Copyright 2025, gematik GmbH
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
 *
 * *******
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.openhealth.requirements

fun String.isCommentLine(prefix: String): Boolean {
    val trimmedLine = this.trim()
    return trimmedLine.startsWith(prefix)
}

fun String.isCommentLineWithControlPrefix(prefix: String): Boolean {
    val trimmedLine = this.trim()
    return trimmedLine.isCommentLine(prefix) && trimmedLine.contains("|")
}

fun String.extractRequirements(): List<String> = this.split(":")[1].mapCommaSeparatedValues()

fun List<String>.indexOfFirstNonCommentLine(startIndex: Int): Int? =
    this
        .drop(startIndex)
        .indexOfFirst { it.isNotEmpty() && !it.isCommentLine("//") }
        .takeIf { it != -1 }
        ?.plus(startIndex)

fun String.hasControlPrefix(): Boolean = this.contains("|")

fun String.splitByColonAndMapCommaSeparatedValues(): List<String> = this.splitByColon().mapCommaSeparatedValues()

fun String.mapCommaSeparatedValues(): List<String> = this.split(",").map { it.trim() }.filter { it.isNotEmpty() }

private fun String.splitByColon(): String = this.split(":")[1].trim()

fun String.removeCommentAndControlPrefix(prefix: String): String =
    this
        .removeCommentPrefix(prefix)
        .trim()
        .removePrefix("|")
        .trim()

fun String.removeCommentPrefix(prefix: String): String {
    val trimmed = this.trim()
    if (trimmed.startsWith(prefix)) {
        return trimmed.removePrefix(prefix).trim()
    }
    return this
}
