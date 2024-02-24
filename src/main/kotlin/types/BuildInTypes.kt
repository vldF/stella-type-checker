package types

object NatType : IType {
    override val name: String = "Nat"

    override fun equals(other: Any?): Boolean {
        return other != null && other is NatType
    }
}

object BoolType : IType {
    override val name: String = "Bool"

    override fun equals(other: Any?): Boolean {
        return other != null && other is BoolType
    }
}
