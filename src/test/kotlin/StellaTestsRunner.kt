import checkers.StellaChecker
import checkers.errors.ErrorStrings
import checkers.errors.StellaError
import checkers.errors.StellaErrorType
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import types.IType
import types.UnknownType
import java.io.File
import java.util.*

object StellaTestsRunner {
    private val supportedExtensions = setOf(
        StellaExtension.UnitType,
        StellaExtension.Pairs,
        StellaExtension.Tuples,
        StellaExtension.Records,
        StellaExtension.LetBindings,
        StellaExtension.NaturalLiterals,
        StellaExtension.TypeAscriptions,
        StellaExtension.NestedFunctionDeclarations,
    )

    fun runOkTest(testName: String) {
        val file = File(okTestsPath).resolve(testName + FILE_EXTENSION_WITH_PERIOD)

        val (errors, parser) = runAnalysis(file)

        Assertions.assertEquals(0, errors.size) {
            val errorsAsText = errors.formatToString(parser)
            "unexpected errors count. Errors: $errorsAsText"
        }
    }

    fun runBadTest(errorType: StellaErrorType, testName: String) {
        val file = File(badTestsPath).resolve(errorType.toString()).resolve(testName + FILE_EXTENSION_WITH_PERIOD)

        val (errors, parser) = runAnalysis(file)

        Assertions.assertTrue(errors.isNotEmpty()) {
            "No errors got"
        }

        val expectedErrors = getAlternativeErrors(file).toSet().plus(errorType)
        val actualErrors = errors.map { it.type }.toSet()
        val errorsAsText = errors.formatToString(parser)

        println(errorsAsText)

        Assertions.assertFalse(errors.map { it.args }.any { args -> args.any { arg -> arg is UnknownType } }) {
            "UnknownType is got!"
        }

        if (!expectedErrors.containsAll(actualErrors)) {
            Assertions.fail<Unit> {
                "expected error $errorType, but got: \n$errorsAsText"
            }
        }
    }

    private fun runAnalysis(file: File): Pair<List<StellaError>, stellaParser> {
        val programTextStream = file.inputStream()

        val lexer = stellaLexer(CharStreams.fromStream(programTextStream))

        val tokens = CommonTokenStream(lexer)
        val parser = stellaParser(tokens)

        val lexerErrorListener = ErrorListener()
        lexer.addErrorListener(lexerErrorListener)

        val parserErrorListener = ErrorListener()
        parser.addErrorListener(parserErrorListener)

        val program = parser.program()

        val testExtensions = program.getExtensions()
        val unsupportedExtensions = testExtensions - supportedExtensions

        Assumptions.assumeTrue(
            !enableOnlySupportedTests() || unsupportedExtensions.isEmpty(),
            "unsupported extensions were found: $unsupportedExtensions"
        )

        val checker = StellaChecker()
        val errors = checker.check(program)

        return errors to parser
    }

    private class ErrorListener : DiagnosticErrorListener(/* exactOnly = */ false) {
        var hasErrors = false

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?,
        ) {
            hasErrors = true

            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        }

        override fun reportAmbiguity(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            exact: Boolean,
            ambigAlts: BitSet?,
            configs: ATNConfigSet?,
        ) { }

        override fun reportAttemptingFullContext(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            conflictingAlts: BitSet?,
            configs: ATNConfigSet?,
        ) { }

        override fun reportContextSensitivity(
            recognizer: Parser?,
            dfa: DFA?,
            startIndex: Int,
            stopIndex: Int,
            prediction: Int,
            configs: ATNConfigSet?,
        ) { }
    }

    private fun List<StellaError>.formatToString(parser: stellaParser): String {
        return this.joinToString(separator = "\n") { it.formatToString(parser) }
    }

    private fun StellaError.formatToString(parser: stellaParser) = buildString {
        appendLine("An error occurred during typechecking!")
        appendLine("ERROR: $type")

        val formattedArgs = args.map { arg ->
            when (arg) {
                is ParserRuleContext -> {
                    val start = arg.start
                    val stop = arg.stop

                    parser.tokenStream.getText(start, stop)
                }
                is IType -> arg.name
                else -> arg
            }
        }.map { "\n$it\n" }
        val asString = ErrorStrings.strings[type]!!.format(*formattedArgs.toTypedArray())
        appendLine(asString)
    }

    private fun getAlternativeErrors(file: File): List<StellaErrorType> {
        val firstLine = file
            .readText()
            .lines()
            .first()

        val commentPrefix = "//"
        if (!firstLine.startsWith(commentPrefix)) {
            return emptyList()
        }

        val errors = firstLine
            .removePrefix(commentPrefix)
            .trim()
            .replace(",", "")
            .split(",")

        return errors.map { StellaErrorType.valueOf(it) }
    }

    private fun stellaParser.ProgramContext.getExtensions(): List<StellaExtension> {
        val anExtensionContext = extension()
        return anExtensionContext
            .filterIsInstance<stellaParser.AnExtensionContext>()
            .flatMap { it.extensionNames }
            .map { it.text.removePrefix("#") }
            .map { StellaExtension.fromString(it) }
    }

    private fun enableOnlySupportedTests(): Boolean {
        return System.getenv()["ENABLE_ONLY_SUPPORTED_TESTS"]?.toBoolean() == true
    }
}
