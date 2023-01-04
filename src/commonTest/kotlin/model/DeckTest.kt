package model

import kotlin.test.Test
import kotlin.test.assertEquals

class DeckTest {

    @Test
    fun serialize() {
        val deck = Deck()
        deck.shuffle(3)
        deck.skipTo(7)

        val value = deck.serialize()
        val deserialized = Deck.deserialize(value)

        assertEquals(deck, deserialized)
    }
}