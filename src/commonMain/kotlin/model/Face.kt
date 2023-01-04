package model

enum class Face(val symbol: String, val order: Int, val value: Int) {
    TWO("3", 0, 0),
    FOUR("4", 1, 0),
    FIVE("5", 2, 0),
    SIX("6", 3, 0),
    SEVEN("7", 4, 0),
    PRINCE("J", 5, 2),
    HORSE("Q", 6, 3),
    KING("K", 7, 4),
    THREE("3", 8, 10),
    ACE("A", 9, 11);

    override fun toString(): String = symbol

    companion object {
        fun fromOrdinal(ordinal: Int): Face = Face.values()[ordinal]
    }
}