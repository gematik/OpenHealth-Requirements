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
