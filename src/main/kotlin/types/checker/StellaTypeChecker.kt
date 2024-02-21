package types.checker

import stellaParser.ProgramContext

class StellaTypeChecker {
    private val errorManager = ErrorManager()
    private val checkerVisitor = TypeCheckerVisitor(errorManager)

    fun analyze(programContext: ProgramContext) {
        checkerVisitor.visitProgram(programContext)
    }

    fun getErrors() = errorManager.getAllErrors()
}
