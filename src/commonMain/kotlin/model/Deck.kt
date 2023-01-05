package model

import kotlin.random.Random

class Deck {
    private val cards = mutableListOf<Card>()
    private var seed = 0
    private var index = 0

    constructor() {
        reset()
    }

    private constructor(seed: Int, index: Int) : this() {
        shuffle(seed)
        skipTo(index)
    }

    override fun equals(other: Any?): Boolean = other is Deck
        && seed == other.seed
        && index == other.index

    fun shuffle(seed: Int) {
        reset() // start fresh (easier to reproduce a deck)
        this.seed = seed
        cards.shuffle(Random(this.seed))
    }

    fun skipTo(index: Int) {
        if (index >= 0 && index < cards.size) {
            this.index = index
        }
    }

    fun deal(): Card = if (isEmpty()) bottom() else cards[index++]

    fun bottom(): Card = cards.last()

    fun isEmpty(): Boolean = remainingCards() <= 0

    fun remainingCards(): Int = cards.size - index

    fun size(): Int = cards.size

    fun serialize(): Pair<Int, Int> = Pair(seed, index)

    override fun toString(): String {
        return buildString {
            for ((i, card) in cards.withIndex()) {
                if (i == index) append(">")
                append(card)
                if (i < cards.size - 1) append(",")
            }
        }
    }

    private fun reset() {
        seed = 0
        index = 0
        cards.clear()
        for (suit in Suit.values()) {
            for (face in Face.values()) {
                cards.add(Card(suit, face))
            }
        }
    }

    companion object {
        fun deserialize(value: Pair<Int, Int>): Deck = Deck(value.first, value.second)
    }
}