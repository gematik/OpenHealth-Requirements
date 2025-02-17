package de.gematik.openhealth.requirements

import kotlin.test.Test
import kotlin.test.assertEquals

class RequirementExtractorTest {
    private val extractor = RequirementExtractor()

    @Test
    fun `no requirements in file`() {
        val source = """
            ...
            fun unrelatedFunction() = println("Nothing to see here.")
            ...
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(0, requirements.size)
    }

    @Test
    fun `single requirement with one ID`() {
        val source = """
            import kotlin.io.path.createTempFile
            
            fun exampleFunction() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_1234
            // | gemSpec_Example
            // | This is a single-line description.
            fun anotherExampleFunction() = println("Hello, world!")
            // REQ-END: GS-A_1234

        """.trimIndent()
        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(1, requirements.size)
        val requirement = requirements.first()
        assertEquals("GS-A_1234", requirement.reqId)
        assertEquals("gemSpec_Example", requirement.spec)
        assertEquals("This is a single-line description.", requirement.desc)
        assertEquals("path", requirement.filePath)
        assertEquals(7, requirement.startLine)
        assertEquals(7, requirement.endLine)
    }

    @Test
    fun `single requirement within function `() {
        val source = """
            import kotlin.io.path.createTempFile
            
            fun exampleFunction() : Int = {
                val a = 1
                val b = 2
                // REQ-BEGIN: GS-A_1234
                // |  gemSpec_Example
                // |This is a single-line description.
                val c = doSomeThingWith(a, b)
                // REQ-END: GS-A_1234
                return c
            }
        """.trimIndent()
        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(1, requirements.size)
        val requirement = requirements.first()
        assertEquals("GS-A_1234", requirement.reqId)
        assertEquals("gemSpec_Example", requirement.spec)
        assertEquals("This is a single-line description.", requirement.desc)
        assertEquals("path", requirement.filePath)
        assertEquals(8, requirement.startLine)
        assertEquals(8, requirement.endLine)
    }

    @Test
    fun `single requirement with multiple IDs`() {
        val source = """
            import kotlin.io.path.createTempFile
            fun exampleFunction1() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_1234, GS-A_5678
            // | gemSpec_Example
            // | Description for multiple IDs.
            fun exampleFunction2() = println("Hello, world!")
            // REQ-END: GS-A_1234
            
            fun anotherExampleFunction() = println("Goodbye!")
            // REQ-END: GS-A_5678
            
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(2, requirements.size)

        val requirement1 = requirements[0]
        assertEquals("GS-A_1234", requirement1.reqId)
        assertEquals("gemSpec_Example", requirement1.spec)
        assertEquals("Description for multiple IDs.", requirement1.desc)
        assertEquals(6, requirement1.startLine)
        assertEquals(6, requirement1.endLine)

        val requirement2 = requirements[1]
        assertEquals("GS-A_5678", requirement2.reqId)
        assertEquals("gemSpec_Example", requirement2.spec)
        assertEquals("Description for multiple IDs.", requirement2.desc)
        assertEquals(6, requirement2.startLine)
        assertEquals(9, requirement2.endLine)
    }

    @Test
    fun `single requirement with more multiple IDs`() {
        val source = """
            import kotlin.io.path.createTempFile
            fun exampleFunction1() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_1234, GS-A_5678, GS-A_91011, 
            // GS-A_121314, GS-A_151617, GS-A_181920
            // | gemSpec_Example
            // | Description for multiple IDs.
            // and more description
            fun exampleFunction2() = println("Hello, world!")
            // REQ-END: GS-A_1234
            
            fun anotherExampleFunction() = println("Goodbye!")
            // REQ-END: GS-A_5678,   GS-A_91011,  GS-A_121314, GS-A_151617,   GS-A_181920
            
        """.trimIndent()
        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(6, requirements.size)

        val requirement1 = requirements[0]
        assertEquals("GS-A_1234", requirement1.reqId)
        assertEquals("gemSpec_Example", requirement1.spec)
        assertEquals("Description for multiple IDs. and more description", requirement1.desc)
        assertEquals(8, requirement1.startLine)
        assertEquals(8, requirement1.endLine)

        val requirement2 = requirements[1]
        assertEquals("GS-A_5678", requirement2.reqId)
        assertEquals("gemSpec_Example", requirement2.spec)
        assertEquals("Description for multiple IDs. and more description", requirement2.desc)
        assertEquals(8, requirement2.startLine)
        assertEquals(11, requirement2.endLine)

        val requirement3 = requirements[2]
        assertEquals("GS-A_91011", requirement3.reqId)
        assertEquals("gemSpec_Example", requirement3.spec)
        assertEquals("Description for multiple IDs. and more description", requirement3.desc)
        assertEquals(8, requirement3.startLine)
        assertEquals(11, requirement3.endLine)

        val requirement4 = requirements[3]
        assertEquals("GS-A_121314", requirement4.reqId)
        assertEquals("gemSpec_Example", requirement4.spec)
        assertEquals("Description for multiple IDs. and more description", requirement4.desc)
        assertEquals(8, requirement4.startLine)
        assertEquals(11, requirement4.endLine)

        val requirement5 = requirements[4]
        assertEquals("GS-A_151617", requirement5.reqId)
        assertEquals("gemSpec_Example", requirement5.spec)
        assertEquals("Description for multiple IDs. and more description", requirement5.desc)
        assertEquals(8, requirement5.startLine)
        assertEquals(11, requirement5.endLine)

        val requirement6 = requirements[5]
        assertEquals("GS-A_181920", requirement6.reqId)
        assertEquals("gemSpec_Example", requirement6.spec)
        assertEquals("Description for multiple IDs. and more description", requirement6.desc)
        assertEquals(8, requirement6.startLine)
        assertEquals(11, requirement6.endLine)
    }


    @Test
    fun `multi-line description`() {
        val source = """
            import kotlin.io.path.createTempFile
            
            fun exampleFunction1() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_91011
            // | gemSpec_Example
            // | This is a multi-line description.
            // It includes more details across multiple lines.
            // And even more details.
            fun anotherExample() = println("Goodbye!")
            // REQ-END: GS-A_91011
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(1, requirements.size)
        val requirement = requirements.first()
        assertEquals("GS-A_91011", requirement.reqId)
        assertEquals("gemSpec_Example", requirement.spec)
        assertEquals(
            "This is a multi-line description. " +
                    "It includes more details across multiple lines. And even more details.",
            requirement.desc
        )
        assertEquals(9, requirement.startLine)
        assertEquals(9, requirement.endLine)
    }

    @Test
    fun `multi-line description with multi-line function`() {
        val source = """
            import kotlin.io.path.createTempFile
            
            fun exampleFunction1() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_91011
            // | gemSpec_Example
            // | This is a multi-line description.
            // It includes more details across multiple lines.
            fun anotherExample() = {
                println("Goodbye!")
                println("Goodbye again!")
            }
            // REQ-END: GS-A_91011
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(1, requirements.size)
        val requirement = requirements.first()
        assertEquals("GS-A_91011", requirement.reqId)
        assertEquals("gemSpec_Example", requirement.spec)
        assertEquals(
            "This is a multi-line description. It includes more details across multiple lines.",
            requirement.desc
        )
        assertEquals(8, requirement.startLine)
        assertEquals(11, requirement.endLine)
    }

    @Test
    fun `multiple requirements in one file`() {
        val source = """
            import kotlin.io.path.createTempFile
            
            fun exampleFunction() = println("Hello, world!")
            
            // REQ-BEGIN: GS-A_1234
            // | gemSpec_Example
            // | First description is a multiline
            // description.
            fun String.exampleFunction1() = 
                this.split(" ").forEach(::println)
            // REQ-END: GS-A_1234
            val a = 1
            // REQ-BEGIN: GS-A_5678
            // | gemSpec_Other
            // | Second description.
            fun exampleFunction2() = println("Goodbye! + a")
            // REQ-END: GS-A_5678
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(2, requirements.size)

        val requirement1 = requirements[0]
        assertEquals("GS-A_1234", requirement1.reqId)
        assertEquals("gemSpec_Example", requirement1.spec)
        assertEquals("First description is a multiline description.", requirement1.desc)
        assertEquals(8, requirement1.startLine)
        assertEquals(9, requirement1.endLine)

        val requirement2 = requirements[1]
        assertEquals("GS-A_5678", requirement2.reqId)
        assertEquals("gemSpec_Other", requirement2.spec)
        assertEquals("Second description.", requirement2.desc)
        assertEquals(15, requirement2.startLine)
        assertEquals(15, requirement2.endLine)
    }

    @Test
    fun `end tag without matching start tag should be ignored`() {
        val source = """
            fun exampleFunction1() = println("Hello, world!")
            // REQ-END: GS-A_1234
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(0, requirements.size)
    }

    @Test
    fun `start tag without end tag should be ignored`() {
        val source = """
            // REQ-BEGIN: GS-A_1234
            // | gemSpec_Example
            // | This requirement has no end tag.
            fun exampleFunction1() = println("Hello, world!")
        """.trimIndent()

        val requirements = extractor.extractRequirements(sequenceOf(Pair(source, "path")), "//")

        assertEquals(0, requirements.size)
    }
}