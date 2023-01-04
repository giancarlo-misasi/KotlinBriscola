package model

import kotlin.test.Test
import kotlin.test.assertEquals

class TrickTest {

    @Test
    fun serialize() {
        val trick = Trick()
        trick.trump = Suit.DIAMOND
        trick.add(Card(Suit.CLUB, Face.FIVE))
        trick.add(Card(Suit.HEART, Face.SIX))
        trick.add(Card(Suit.SPADE, Face.SEVEN))
        trick.add(Card(Suit.DIAMOND, Face.ACE))

        val value = trick.serialize()
        val deserialized = Trick.deserialize(value)

        assertEquals(trick, deserialized)
    }
}