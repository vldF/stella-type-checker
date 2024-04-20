package types


@Suppress("KotlinConstantConditions")
fun IType.isSubtypeOf(other: IType, subtypingEnabled: Boolean): Boolean {
    return when {
        this == other -> true
        !subtypingEnabled -> false
        this is BotType -> true
        other is TopType -> true

        this is FunctionalType && other is FunctionalType -> {
            other.from.isSubtypeOf(this.from, subtypingEnabled) && this.to.isSubtypeOf(other.to, subtypingEnabled)
        }

        this is ReferenceType && other is ReferenceType -> {
            this.innerType.isSubtypeOf(other.innerType, subtypingEnabled)
                    && other.innerType.isSubtypeOf(this.innerType, subtypingEnabled)
        }

        this is RecordType && other is RecordType -> {
            // todo: check it
            val thisLabels = this.labels.zip(this.types).toSet()
            val otherLabels = other.labels.zip(other.types).toSet()

            thisLabels.containsAll(otherLabels)
        }

        this is VariantType && other is VariantType -> {
            val thisLabels = this.labels.zip(this.types).toSet()
            val otherLabels = other.labels.zip(other.types).toSet()

            otherLabels.containsAll(thisLabels)
        }

        this is TupleType && other is TupleType -> {
            this.types.size == other.types.size &&
                    this.types.zip(other.types).all { (t1, t2) -> t1.isSubtypeOf(t2, subtypingEnabled) }
        }

        this is ListType && other is ListType -> {
            this.type.isSubtypeOf(other.type, subtypingEnabled)
        }

        this is SumType && other is SumType -> {
            this.left.isSubtypeOf(other.left, subtypingEnabled)
                    && this.right.isSubtypeOf(other.right, subtypingEnabled)
        }

        else -> false
    }
}

fun IType.isNotSubtypeOf(other: IType, subtypingEnabled: Boolean) = !this.isSubtypeOf(other, subtypingEnabled)
