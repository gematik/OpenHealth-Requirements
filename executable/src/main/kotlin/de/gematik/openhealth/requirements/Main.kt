package de.gematik.openhealth.requirements

fun main(args: Array<String>) {
    if (args.size < 3) {
        println("Usage: <commentPrefix> <outputFile> <file1Content>:<file1Path> <file2Content>:<file2Path> ...")
        return
    }

    val commentPrefix = args[0]
    val outputFile = args[1]

    val files = args.drop(2).map {
        val parts = it.split(":", limit = 2)
        if (parts.size != 2) {
            println("Error: Invalid file argument format! Expected <content>:<path>, but got '$it'")
            return
        }
        parts[0] to parts[1]
    }.asSequence()

    val extractor = RequirementExtractor()
    val requirements = extractor.extractRequirements(files, commentPrefix)

    println("Extracted ${requirements.size} requirements. Saving to $outputFile...")

    extractor.writeToCsv(requirements, outputFile)

    println("📁 Results saved in: $outputFile")
}