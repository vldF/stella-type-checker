package checkers.structure

import checkers.abstract.IStellaChecker
import checkers.errors.ErrorManager
import stellaParser.ProgramContext

class StellaProgramStructureChecker(
    errorManager: ErrorManager
) : IStellaChecker {
    private val visitor = MainFunctionCheckerVisitor(errorManager)

    override fun check(programContext: ProgramContext) {
        visitor.visitProgram(programContext)
    }
}
