package types

object TopType : IType {
    override val name: String = "Top"

    override fun equals(other: Any?): Boolean {
        return other != null && other is TopType
    }

    override fun toString(): String = name
}
