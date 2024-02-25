package types

data object UnknownType : IType(isKnownType = false) {
    override val name: String = "UnknownType"
}
