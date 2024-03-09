package checkers.types

import checkers.abstract.IStellaChecker
import checkers.errors.ErrorManager
import stellaParser.ProgramContext
import types.TypeContext

class StellaTypeChecker(
    errorManager: ErrorManager
) : IStellaChecker {
    private val checkerVisitor = TypeChecker(errorManager)

    override fun check(programContext: ProgramContext) {
        checkerVisitor.checkProgram(programContext)
    }
}
