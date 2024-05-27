package types

class UniversalWrapperType(
    val typeParams: List<GenericType>,
    val innerType: IType
) : IType {
    override val name: String = "[${typeParams.joinToString(",")}]$innerType"

    override fun equals(other: Any?): Boolean {
        return other != null
                && other is UniversalWrapperType
                && this.typeParams == other.typeParams
                && this.innerType == other.innerType
    }

    override fun toString(): String = name

    override fun hashCode(): Int {
        return typeParams.hashCode() + innerType.hashCode()
    }
}
