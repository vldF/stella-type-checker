package types

sealed class IType(protected val isKnownType: Boolean = true) {
    abstract val name: String

}
