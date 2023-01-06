package model

data class Card(val suit: Suit, val face: Face) {

    val materialName by lazy { "${suit.alias}${face.symbol}" }

    // NOTE: This function only works if you do winner.isWorseThan
    // because it assumes that the only way to be better
    // is if you are the same suit with better face or if you are a trump suit
    fun isWorseThan(other: Card, trump: Suit): Boolean {
        if (suit == other.suit) {
            return other.face > face;
        }
        return other.suit == trump;
    }

    fun serialize(): Int = suit.ordinal * 10 + face.ordinal

    override fun toString(): String = "$face$suit"

    companion object {
        fun deserialize(value: Int): Card = Card(
            Suit.fromOrdinal(value / 10),
            Face.fromOrdinal(value % 10)
        )
    }
}