package types

object BotType : IType {
    override val name: String = "Bot"

    override fun equals(other: Any?): Boolean {
        return other != null && other is BotType
    }

    override fun toString(): String = name
}
