import java.io.File

private const val INDENT = "    "

fun main() {
    val code = StringBuilder()
    code.addPreamble()
    code.generateOkTests()
    code.generateBadTests()

    val resultFilePath = File(testCodeBaseDir).resolve("StellaTests.kt")
    resultFilePath.writeText(code.toString())
}

private fun StringBuilder.generateOkTests() {
    val files = File(okTestsPath).listFiles { file -> file.extension == FILE_EXTENSION } ?: error("no ok tests")

    addTestClass("OK_TESTS") {
        files.forEach(::addOkTestFunction)
    }
}

private fun StringBuilder.generateBadTests() {
    val errorTypes = File(badTestsPath).listFiles { file -> file.isDirectory } ?: error("no ok tests")

    for (errorDir in errorTypes) {
        val errorType = StellaTypeError.valueOf(errorDir.name)
        val files = errorDir.listFiles { file -> file.extension == FILE_EXTENSION } ?: continue

        addTestClass("${errorType}_TESTS") {
            files.forEach { file -> addBadTestFunction(errorType, file) }
        }

    }
}

private fun StringBuilder.addPreamble() {
    val content = """
        import org.junit.jupiter.api.Test
        
        // DO NOT MODIFY THIS FILE MANUALLY
        // Edit TestsGenerator.kt instead
    """.trimIndent()

    appendLine(content)
    appendLine()
}

private fun StringBuilder.addTestClass(name: String, content: StringBuilder.() -> Unit) {
    val contentStringBuilder = StringBuilder()
    contentStringBuilder.content()
    val contentLines = contentStringBuilder.lines()

    appendLine("@Suppress(\"ClassName\")")
    appendLine("class $name {")
    contentLines.map { INDENT + it }.forEach(::appendLine)
    appendLine("}")
}

private fun StringBuilder.addOkTestFunction(file: File) {
    val content = """
        @Test
        fun ${file.nameWithoutExtension}_test() {
            StellaTestsRunner.runOkTest("${file.nameWithoutExtension}")
        }
    """.trimIndent()

    appendLine(content)
}

private fun StringBuilder.addBadTestFunction(errorType: StellaTypeError, file: File) {
    val content = """
        @Test
        fun ${file.nameWithoutExtension}_test() {
            StellaTestsRunner.runBadTest(StellaTypeError.$errorType, "${file.nameWithoutExtension}")
        }
    """.trimIndent()

    appendLine(content)
}
