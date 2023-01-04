package model

enum class Suit(val symbol: String) {
    HEART("♥"),     // Cups
    SPADE("♠"),     // Swords
    CLUB("♣"),      // Clubs
    DIAMOND("♦");   // Coins

    override fun toString(): String = symbol

    companion object {
        fun fromOrdinal(ordinal: Int): Suit = Suit.values()[ordinal]
    }
}