package types

class RecordType(
    val labels: Array<String>,
    val types: Array<IType>,
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

        return this.labels contentEquals other.labels && this.types contentEquals other.types
    }

    override fun hashCode(): Int {
        var result = labels.contentHashCode()
        result = 31 * result + types.contentHashCode()
        return result
    }
}
