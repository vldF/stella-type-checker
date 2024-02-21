package types.checker

import stellaParser.ProgramContext

class StellaTypeChecker {
    private val errorManager = ErrorManager()

    fun analyze(programContext: ProgramContext) {

    }

    fun getErrors() = errorManager.getAllErrors()
}
