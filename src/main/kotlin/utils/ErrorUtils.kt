package utils

import checkers.errors.ErrorStrings
import checkers.errors.StellaError
import org.antlr.v4.runtime.ParserRuleContext
import stellaParser
import types.IType

fun StellaError.formatToString(parser: stellaParser, isDebug: Boolean = false) = buildString {
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

    if (isDebug) {
        appendLine(this@formatToString.stackTrace.joinToString("\n"))
    }
}

fun Collection<StellaError>.formatToString(parser: stellaParser, isDebug: Boolean = false): String {
    return this.joinToString(separator = "\n") { it.formatToString(parser, isDebug) }
}
