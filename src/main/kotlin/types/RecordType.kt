package types

class RecordType(
    val labels: List<String>,
    val types: List<IType>,
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

        return this.labels == other.labels && this.types == other.types
    }

    override fun hashCode(): Int {
        var result = labels.hashCode()
        result = 31 * result + types.hashCode()
        return result
    }
}
