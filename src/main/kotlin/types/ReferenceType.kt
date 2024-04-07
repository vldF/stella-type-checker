package types

class ReferenceType(
    val innerType: IType
) : IType {
    override val name: String = "&${innerType.name}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReferenceType

        return innerType == other.innerType
    }

    override fun hashCode(): Int {
        val result = innerType.hashCode()
        return result
    }


}
