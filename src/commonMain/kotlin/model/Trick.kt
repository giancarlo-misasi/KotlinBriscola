package model

class Trick {
    private val cards = mutableListOf<Card>()

    var trump: Suit = Suit.SPADE
    var winner: Card? = null
        private set

    constructor()

    private constructor(trump: Suit, cards: List<Card>) {
        this.trump = trump
        cards.forEach { add(it) }
    }

    override fun equals(other: Any?): Boolean = other is Trick
            && cards == other.cards
            && trump == other.trump
            && winner == other.winner

    fun value(): Int = cards.sumOf { it.face.value }
    fun cards(): List<Card> = cards
    fun full(numberOfPlayers: Int) = cards.size == numberOfPlayers

    fun add(card: Card): Boolean {
        cards.add(card)
        val w = winner
        return if (w == null || w.isWorseThan(card, trump)) {
            winner = w
            true
        } else {
            false
        }
    }

    fun clear() {
        cards.clear()
        winner = null
    }

    fun serialize(): Int {
        // serializing approach:
        //           3 - trump suit is 3
        //         400 - no card
        //      39_000 - suit = 3, face = 9
        //   2_100_000 - suit = 2, face = 1
        // 140_000_000 - suit = 1, face = 4
        var sum = trump.ordinal
        var multiplier = 10
        for (i in 0 until 4) {
            sum += if (i < cards.size) {
                // add in reverse order to deserialize in the correct order
                multiplier * cards[cards.size - i - 1].serialize()
            } else {
                multiplier * 40
            }
            multiplier *= 100
        }
        return sum
    }

    companion object {
        fun deserialize(value: Int): Trick {
            var remaining = value
            var divisor = 10_000_000
            val cards = mutableListOf<Card>()
            for (i in 0 until 4) {
                val c = remaining / divisor
                if (c in 0..39) {
                    cards.add(Card.deserialize(c))
                }
                remaining %= divisor
                divisor /= 100
            }

            return Trick(Suit.fromOrdinal(remaining), cards)
        }
    }
}