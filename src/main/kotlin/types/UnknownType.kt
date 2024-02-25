package types

object UnknownType : IType(isKnownType = false) {
    override val name: String = "UnknownType"

    override fun equals(other: Any?): Boolean {
        return other is IType
    }
}
