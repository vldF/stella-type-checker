package types

class TypeContext(
    private val parent: TypeContext? = null
) {
    private val variableTypes = mutableMapOf<String, SimpleType>()

    fun saveVariableType(variableName: String, type: SimpleType) {
        if (variableTypes.containsKey(variableName)) {
            error("already known variable $variableName with type $type")
        }

        variableTypes[variableName] = type
    }

    fun resolveVariableType(name: String): SimpleType? {
        return variableTypes[name] ?: parent?.resolveVariableType(name)
    }
}
