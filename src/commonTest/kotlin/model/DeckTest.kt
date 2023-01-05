package model

import kotlin.test.Test
import kotlin.test.assertEquals

class DeckTest {

    @Test
    fun serialize() {
        testSerialize(Int.MIN_VALUE, 3)
        testSerialize(Int.MAX_VALUE, 4)
    }

    private fun testSerialize(seed: Int, skipTo: Int) {
        val deck = Deck()
        deck.shuffle(seed)
        deck.skipTo(skipTo)
        val value = deck.serialize()
        val deserialized = Deck.deserialize(value)
        assertEquals(deck, deserialized)
    }
}