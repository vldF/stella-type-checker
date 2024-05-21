package types

class TypeVar private constructor(val index: Int) : IType {
    override val name: String
        get() = "?T$index"

    override fun equals(other: Any?): Boolean {
        return other != null && other is TypeVar && other.index == index
    }

    override fun toString(): String = name

    override fun hashCode(): Int = index.hashCode()

    companion object {
        private var counter = 0

        fun new(): TypeVar = TypeVar(counter++)
    }
}
