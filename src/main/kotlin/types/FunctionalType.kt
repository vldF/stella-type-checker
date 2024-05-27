package types

open class FunctionalType(
    val from: IType,
    val to: IType
) : IType {
    override val name: String = "(${from.name}) -> ${to.name}"

    fun withSubstitution(types: Map<GenericType, IType>): FunctionalType {
        var newFrom = from
        var newTo = to

        for ((gType, type) in types) {
            newFrom = newFrom.substitute(gType, type)
            newTo = newTo.substitute(gType, type)
        }

        return FunctionalType(newFrom, newTo)
    }

    private fun IType.substitute(generic: GenericType, new: IType): IType {
        return when (this) {
            is FunctionalType -> FunctionalType(from.substitute(generic, new), to.substitute(generic, new))
            is GenericType -> if (this == generic) new else this
            is ListType -> return ListType(this.type.substitute(generic, new))
            is SumType -> SumType(left.substitute(generic, new), right.substitute(generic, new))
            is TupleType -> TupleType(types.map { it.substitute(generic, new) })
            is VariantType -> VariantType(labels, types.map { it.substitute(generic, new) })
            is UniversalWrapperType -> UniversalWrapperType(typeParams.filter { it != generic }, innerType.substitute(generic, new))
            else -> this
        }
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
