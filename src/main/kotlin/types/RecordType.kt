package types

class RecordType(
    val labels: Array<String>,
    val types: Array<IType>
) : IType {
    init {
        check(labels.size == types.size)
    }

    override val name: String = labels
        .zip(types)
        .joinToString(separator = ", ", prefix = "{", postfix = "}") { "${it.first} : ${it.second.name}" }

    override fun equals(other: Any?): Boolean {
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
