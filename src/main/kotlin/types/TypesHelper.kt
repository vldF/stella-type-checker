package types


fun IType.isSubtypeOf(other: IType): Boolean {
    return when {
        this == other -> true
        this is BotType -> true
        other is TopType -> true

        this is FunctionalType && other is FunctionalType -> {
            this.from.isSubtypeOf(other.from) && other.to.isSubtypeOf(this.to)
        }

        else -> false
    }
}

fun areTypesEquivalent(first: IType, second: IType): Boolean {
    return when {
        first.isSubtypeOf(second) -> true
        second.isSubtypeOf(first) -> true
        else -> first == second
    }
}
