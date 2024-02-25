package types

class FunctionalType(
    val from: IType,
    val to: IType,
    isKnownType: Boolean = true
) : IType(isKnownType) {
    override val name: String = if (isKnownType) {
        "(${from.name}) -> $to"
    } else {
        "(?) -> (?)"
    }

    internal constructor(isKnownType: Boolean) : this(UnknownType, UnknownType, isKnownType = false)

    override fun equals(other: Any?): Boolean {
        return other != null && other is FunctionalType && (this.from == other.from && this.to == other.to)
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
