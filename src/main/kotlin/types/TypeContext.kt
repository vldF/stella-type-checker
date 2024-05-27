package types

class TypeContext(
    private val parent: TypeContext? = null
) {
    private val variableTypes = mutableMapOf<String, IType>()
    private val functionTypes = mutableMapOf<String, IType>()
    private val genericTypes = mutableMapOf<String, GenericType>()

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

    fun saveFunctionType(functionName: String, type: IType) {
        if (functionTypes.containsKey(functionName)) {
            error("already known function $functionName of type $type")
        }

        functionTypes[functionName] = type
    }

    fun resolveFunctionType(name: String): IType? {
        return functionTypes[name] ?: parent?.resolveFunctionType(name)
    }

    fun saveGenericType(type: GenericType) {
        if (genericTypes.containsKey(type.varName)) {
            error("already known generic $type with type $type")
        }

        genericTypes[type.varName] = type
    }

    fun resolveGenericType(type: GenericType): GenericType? {
        return genericTypes[type.varName] ?: parent?.resolveGenericType(type)
    }
}
