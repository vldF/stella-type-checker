import checkers.StellaChecker
import checkers.errors.StellaError
import checkers.errors.StellaErrorType
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.junit.jupiter.api.Assertions
import java.io.File
import java.util.*

object StellaTestsRunner {
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

//        Assertions.assertEquals(1, errors.size) {
//            val errorsAsText = errors.formatToString(parser)
//            "unexpected errors count. Errors: $errorsAsText"
//        }
//
//        val error = errors.first()
//        Assertions.assertEquals(errorType, error.type) {
//            "expected error type $errorType, but got ${error.formatToString(parser)}"
//        }

        Assertions.assertTrue(errors.isNotEmpty()) {
            "No errors got"
        }

        if (errorType !in errors.map { it.type }) {
            Assertions.fail<Unit> {
                val errorsAsText = errors.formatToString(parser)
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
        append("ERROR: $type ")
        append(node.toStringTree(parser))
    }
}
