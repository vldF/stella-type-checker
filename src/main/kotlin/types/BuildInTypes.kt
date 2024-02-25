package types

object NatType : IType() {
    override val name: String = "Nat"

    override fun equals(other: Any?): Boolean {
        if (!isKnownType || other is IType && !other.isKnownType) {
            return true
        }

        return other != null && other is NatType
    }
}

object BoolType : IType() {
    override val name: String = "Bool"

    override fun equals(other: Any?): Boolean {
        if (!isKnownType || other is IType && !other.isKnownType) {
            return true
        }

        return other != null && other is BoolType
    }
}

object UnitType : IType() {
    override val name: String = "Unit"

    override fun equals(other: Any?): Boolean {
        if (!isKnownType || other is IType && !other.isKnownType) {
            return true
        }

        return other != null && other is UnitType
    }
}
