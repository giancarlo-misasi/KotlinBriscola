package model

import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerTest {

    @Test
    fun serialize() {
        val player = Player()
        player.add(Card(Suit.CLUB, Face.FIVE))
        player.add(Card(Suit.HEART, Face.SIX))
        player.add(Card(Suit.SPADE, Face.SEVEN))

        val value = player.serialize()
        val deserialized = Player.deserialize(value)

        assertEquals(player, deserialized)
    }
}