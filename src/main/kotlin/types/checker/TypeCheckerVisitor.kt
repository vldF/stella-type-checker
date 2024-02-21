package types.checker

import stellaParserBaseVisitor

class TypeCheckerVisitor(private val errorManager: ErrorManager) : stellaParserBaseVisitor<Unit>() {
    override fun visitProgram(ctx: stellaParser.ProgramContext) {
        errorManager.registerError(StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE, ctx)
    }
}
