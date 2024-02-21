package types.checker

import org.antlr.v4.runtime.ParserRuleContext

data class StellaError (
    val type: StellaErrorType,
    val node: ParserRuleContext,
)
