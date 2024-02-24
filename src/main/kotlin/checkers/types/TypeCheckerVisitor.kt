package checkers.types

import checkers.errors.ErrorManager
import stellaParserBaseVisitor
import types.TypeContext

class TypeCheckerVisitor(
    private val errorManager: ErrorManager,
) : stellaParserBaseVisitor<Unit>() {
    private val typeContext = TypeContext()


}
