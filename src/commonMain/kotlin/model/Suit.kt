package model

enum class Suit(val symbol: String, val alias: String) {
    HEART("♥", "Cup"),      // Cups
    SPADE("♠", "Sword"),    // Swords
    CLUB("♣", "Club"),      // Clubs
    DIAMOND("♦", "Coin");   // Coins

    override fun toString(): String = symbol

    companion object {
        fun fromOrdinal(ordinal: Int): Suit = Suit.values()[ordinal]
    }
}