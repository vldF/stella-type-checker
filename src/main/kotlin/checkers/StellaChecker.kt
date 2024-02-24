package checkers

import checkers.errors.ErrorManager
import checkers.errors.StellaError
import checkers.structure.StellaProgramStructureChecker
import checkers.types.StellaTypeChecker
import stellaParser.ProgramContext

class StellaChecker {
    private val errorManager = ErrorManager()
    private val checkers = listOf(
        StellaTypeChecker(errorManager),
        StellaProgramStructureChecker(errorManager),
    )

    fun check(programContext: ProgramContext): List<StellaError> {
        checkers.forEach { checker -> checker.check(programContext) }

        return errorManager.getAllErrors()
    }
}
