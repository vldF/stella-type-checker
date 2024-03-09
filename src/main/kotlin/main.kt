import checkers.StellaChecker
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import utils.formatToString
import java.util.*
import kotlin.system.exitProcess

fun main() {
    val programText = readCode()

    val lexer = stellaLexer(CharStreams.fromString(programText))

    val tokens = CommonTokenStream(lexer)
    val parser = stellaParser(tokens)

    val program = parser.program()

    val checker = StellaChecker()
    val errors = checker.check(program)
    val primaryError = errors.firstOrNull()

    if (primaryError == null) {
        println("OK")
        exitProcess(0)
    }

    val formattedError = primaryError.formatToString(parser)
    System.err.println(formattedError)
    exitProcess(1)
}

private fun readCode(): String {
    val code = StringBuilder()
    val scanner = Scanner(System.`in`)
    scanner.useDelimiter(System.lineSeparator())
    while (scanner.hasNext()) {
        code.appendLine(scanner.next())
    }

    scanner.close()
    return code.toString()
}
