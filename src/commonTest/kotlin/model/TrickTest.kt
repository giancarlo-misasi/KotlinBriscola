package model

import kotlin.test.Test
import kotlin.test.assertEquals

class TrickTest {

    @Test
    fun winner() {
        val trick = Trick()
        val c1 = Card(Suit.CLUB, Face.THREE)
        val c2 = Card(Suit.SPADE, Face.ACE)
        val c3 = Card(Suit.DIAMOND, Face.TWO)

        trick.trump = Suit.DIAMOND
        trick.add(c1)
        assertEquals(c1, trick.winner)

        trick.add(c2)
        assertEquals(c1, trick.winner)

        trick.add(c3)
        assertEquals(c3, trick.winner)
    }

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