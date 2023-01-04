package model

data class Card(val suit: Suit, val face: Face) {
    override fun toString(): String = "$suit$face"

    fun isWorseThan(other: Card, trump: Suit): Boolean {
        if (suit == other.suit) {
            return other.face > face;
        }
        return other.suit == trump;
    }

    fun serialize(): Int = suit.ordinal * 10 + face.ordinal

    companion object {
        fun deserialize(value: Int): Card = Card(
            Suit.fromOrdinal(value / 10),
            Face.fromOrdinal(value % 10)
        )
    }
}