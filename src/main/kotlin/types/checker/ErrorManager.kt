package types.checker

import org.antlr.v4.runtime.ParserRuleContext

class ErrorManager {
    private val errorsList = mutableListOf<StellaError>()

    fun registerError(type: StellaErrorType, node: ParserRuleContext) {
        val error = StellaError(type, node)
        errorsList.add(error)
    }

    fun getAllErrors(): List<StellaError> {
        return errorsList.toList()
    }
}
