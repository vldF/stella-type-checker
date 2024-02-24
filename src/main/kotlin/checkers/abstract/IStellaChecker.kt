package checkers.abstract

import stellaParser.ProgramContext

interface IStellaChecker {
    fun check(programContext: ProgramContext)
}
