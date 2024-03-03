package types

class VariantType(
    val labels: List<String>,
    val types: List<IType>,
) : IType() {
    override val name: String = labels
        .zip(types)
        .joinToString(separator = ", ", prefix = "<|", postfix = "|>") { pair -> "${pair.first} : ${pair.second}" }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VariantType

        if (labels != other.labels) return false
        if (types != other.types) return false

        return true
    }

    override fun hashCode(): Int {
        var result = labels.hashCode()
        result = 31 * result + types.hashCode()
        return result
    }

}
