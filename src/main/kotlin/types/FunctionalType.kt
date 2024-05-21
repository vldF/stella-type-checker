package types

class FunctionalType(
    val from: IType,
    val to: IType,
    isKnownType: Boolean = true
) : IType {
    override val name: String = if (isKnownType) {
        "(${from.name}) -> ${to.name}"
    } else {
        "(?) -> (?)"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is FunctionalType && (this.from == other.from && this.to == other.to)
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String = name
}
