package types

class GenericType(
    val varName: String
) : IType {
    override val name: String = "[$varName]"

    override fun equals(other: Any?): Boolean {
        return other != null && other is GenericType && this.varName == other.varName
    }

    override fun toString(): String = name

    override fun hashCode(): Int {
        return varName.hashCode()
    }
}
