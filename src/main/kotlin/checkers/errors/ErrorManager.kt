package checkers.errors

class ErrorManager {
    private val errorsList = mutableListOf<StellaError>()

    fun registerError(type: StellaErrorType, vararg args: Any) {
        val error = StellaError(type, args.toList().toTypedArray())
        errorsList.add(error)
    }

    fun getAllErrors(): List<StellaError> {
        return errorsList.toList()
    }
}
