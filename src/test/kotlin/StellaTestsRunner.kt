import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.io.File
import java.util.*

object StellaTestsRunner {
    fun runOkTest(testName: String) {
        val file = File(okTestsPath).resolve(testName + FILE_EXTENSION_WITH_PERIOD)
        runAnalysis(file)
    }

    fun runBadTest(errorType: StellaTypeError, testName: String) {
        val file = File(badTestsPath).resolve(errorType.toString()).resolve(testName + FILE_EXTENSION_WITH_PERIOD)
        runAnalysis(file)
    }

    private fun runAnalysis(file: File) {
        val programTextStream = file.inputStream()

        val lexer = stellaLexer(UnbufferedCharStream(programTextStream))

        val tokens = CommonTokenStream(lexer)
        val parser = stellaParser(tokens)

        val lexerErrorListener = ErrorListener()
        lexer.addErrorListener(lexerErrorListener)

        val parserErrorListener = ErrorListener()
        parser.addErrorListener(parserErrorListener)

        parser.program()
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
}
