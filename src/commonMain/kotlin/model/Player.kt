package model

class Player {
    private val cards = mutableListOf<Card>()

    var points = 0
        private set

    constructor()

    private constructor(cards: List<Card>) {
        cards.forEach { add(it) }
    }

    override fun equals(other: Any?): Boolean = other is Player
            && cards == other.cards
            && points == other.points

    fun add(card: Card) = cards.add(card)

    fun remove(card: Card) = cards.remove(card)

    fun add(points: Int) {
        this.points += points
    }

    fun reset() {
        points = 0
        cards.clear()
    }

    fun cards(): List<Card> = cards

    fun isEmpty(): Boolean = remainingCards() <= 0

    fun remainingCards(): Int = cards.size

    fun serialize(): Int {
        // serializing approach:
        //      40 - no card
        //   3_900 - suit = 3, face = 9
        // 210_000 - suit = 2, face = 1

        var sum = 0
        var multiplier = 1
        for (i in 0 until 3) {
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
        fun deserialize(value: Int): Player {
            var remaining = value
            var divisor = 10_000
            val cards = mutableListOf<Card>()
            for (i in 0 until 3) {
                val c = remaining / divisor
                if (c in 0..39) {
                    cards.add(Card.deserialize(c))
                }
                remaining %= divisor
                divisor /= 100
            }
            return Player(cards)
        }
    }
}