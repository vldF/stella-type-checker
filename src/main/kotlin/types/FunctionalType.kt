package types

data class FunctionalType(
    val from: IType,
    val to: IType
) : IType {
    override val name: String = "(${from.name}) -> $to"

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
