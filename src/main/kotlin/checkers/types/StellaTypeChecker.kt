package checkers.types

import checkers.abstract.IStellaChecker
import checkers.errors.ErrorManager
import checkers.types.inferrer.UnifySolver
import stellaParser.ProgramContext

class StellaTypeChecker(
    errorManager: ErrorManager
) : IStellaChecker {
    private val checkerVisitor = TypeChecker(
        errorManager,
        parentContext = null,
        extensionManager = ExtensionManager(),
        unifySolver = UnifySolver()
    )

    override fun check(programContext: ProgramContext) {
        checkerVisitor.checkProgram(programContext)
    }
}
