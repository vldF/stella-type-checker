package types

class TypeContext(
    private val parent: TypeContext? = null
) {
    private val variableTypes = mutableMapOf<String, IType>()
    private val functionTypes = mutableMapOf<String, FunctionalType>()

    var exceptionType: IType? = null
        get() = field ?: parent?.exceptionType

    fun saveVariableType(variableName: String, type: IType) {
        if (variableTypes.containsKey(variableName)) {
            error("already known variable $variableName with type $type")
        }

        variableTypes[variableName] = type
    }

    fun resolveVariableType(name: String): IType? {
        return variableTypes[name] ?: parent?.resolveVariableType(name)
    }

    fun saveFunctionType(functionName: String, type: FunctionalType) {
        if (functionTypes.containsKey(functionName)) {
            error("already known variable $functionName with type $type")
        }

        functionTypes[functionName] = type
    }

    fun resolveFunctionType(name: String): FunctionalType? {
        return functionTypes[name] ?: parent?.resolveFunctionType(name)
    }
}
