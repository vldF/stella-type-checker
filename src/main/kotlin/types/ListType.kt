package types

class ListType(
    val type: IType
) : IType {
    override val name: String = "List[${type.name}]"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListType

        return type == other.type
    }

    override fun hashCode(): Int {
        val result = type.hashCode()
        return result
    }

    override fun toString(): String = name
}
