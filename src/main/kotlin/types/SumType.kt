package types

class SumType(
    val left: IType,
    val right: IType
) : IType {
    override val name: String = "(${left.name} + ${right.name})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SumType

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()

        return result
    }

}
