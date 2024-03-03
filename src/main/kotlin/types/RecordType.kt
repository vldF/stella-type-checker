package types

class RecordType(
    val labels: List<String>,
    val types: List<IType>,
    isKnownType: Boolean = true
) : IType(isKnownType) {
    init {
        check(labels.size == types.size)
    }

    override val name: String = if (isKnownType) {
        labels
            .zip(types)
            .joinToString(separator = ", ", prefix = "{", postfix = "}") { "${it.first} : ${it.second.name}" }
    } else {
        "UnknownRecord"
    }

    override fun equals(other: Any?): Boolean {
        if (!isKnownType || other is IType && !other.isKnownType) {
            return true
        }

        if (other == null || other !is RecordType) {
            return false
        }

        return this.labels == other.labels && this.types == other.types
    }

    override fun hashCode(): Int {
        var result = labels.hashCode()
        result = 31 * result + types.hashCode()
        return result
    }
}
