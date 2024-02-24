package checkers.types

import checkers.abstract.IStellaChecker
import checkers.errors.ErrorManager
import stellaParser.ProgramContext

class StellaTypeChecker(
    errorManager: ErrorManager
) : IStellaChecker {
    private val checkerVisitor = TypeCheckerVisitor(errorManager)

    override fun check(programContext: ProgramContext) {
        checkerVisitor.visitProgram(programContext)
    }
}
