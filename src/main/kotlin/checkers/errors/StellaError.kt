package checkers.errors

data class StellaError (
    val type: StellaErrorType,
    val args: Array<Any>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StellaError

        if (type != other.type) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}
