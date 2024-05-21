package checkers.types.inferrer

import org.antlr.v4.runtime.ParserRuleContext
import types.IType

data class Constraint(
    val left: IType,
    val right: IType,
    val ruleContext: ParserRuleContext
)
