package types.checker

class ErrorManager {
    private val errorsList = mutableListOf<StellaError>()

    fun registerError(error: StellaError) {
        errorsList.add(error)
    }

    fun getAllErrors(): List<StellaError> {
        return errorsList.toList()
    }
}
