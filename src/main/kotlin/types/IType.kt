package types

sealed class IType(val isKnownType: Boolean = true) {
    abstract val name: String

}
