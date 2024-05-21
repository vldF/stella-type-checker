package types

object NatType : IType {
    override val name: String = "Nat"

    override fun equals(other: Any?): Boolean {
        return other != null && other is NatType
    }

    override fun toString(): String = name
}

object BoolType : IType {
    override val name: String = "Bool"

    override fun equals(other: Any?): Boolean {
        return other != null && other is BoolType
    }

    override fun toString(): String = name
}

object UnitType : IType {
    override val name: String = "Unit"

    override fun equals(other: Any?): Boolean {
        return other != null && other is UnitType
    }

    override fun toString(): String = name
}
