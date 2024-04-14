package checkers.errors

class ErrorManager {
    private val errorsList = mutableListOf<StellaError>()

    fun registerError(type: StellaErrorType, vararg args: Any) {
        val stacktrace = Thread.currentThread().stackTrace.drop(2)

        val error = StellaError(type, args.toList().toTypedArray(), stacktrace)
        errorsList.add(error)
    }

    fun getAllErrors(): List<StellaError> {
        return errorsList.toList()
    }
}
