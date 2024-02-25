package types

class TupleType(
    val types: Array<IType>,
    isKnownType: Boolean = true
) : IType(isKnownType) {
    val arity: Int = types.size

    override val name: String = if (isKnownType) {
        types.joinToString(separator = ", ", prefix = "{", postfix = "}")
    } else {
        "UnknownTuple"
    }

    internal constructor(isKnownType: Boolean = true) : this(arrayOf(), isKnownType)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TupleType

        if (!this.isKnownType || !other.isKnownType) {
            return true
        }

        return types.contentEquals(other.types)
    }

    override fun hashCode(): Int {
        return types.contentHashCode()
    }
}
