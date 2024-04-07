package types

class TupleType(
    val types: List<IType>
) : IType {
    val arity: Int = types.size

    override val name: String = types.joinToString(separator = ", ", prefix = "{", postfix = "}") { it.name}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TupleType

        return types == other.types
    }

    override fun hashCode(): Int {
        return types.hashCode()
    }
}
