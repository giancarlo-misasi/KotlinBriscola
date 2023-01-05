import model.Phase

import kotlin.test.Test
import kotlin.test.assertEquals

class BriscolaTest {

    @Test
    fun serialize() {
        val briscola = Briscola(2)
        val value = briscola.serialize()
        val deserialized = Briscola.deserialize(value)
        assertEquals(briscola, deserialized)
    }

    @Test
    fun playGame() {
        val s = Briscola.simulateOnce(
            26,
            { s -> s.currentPlayer().cards().first() },
            { s -> s.currentPlayer().cards().first() }
        )
        assertEquals(s.phase, Phase.END)
        assertEquals(41, s.players[0].points)
        assertEquals(79, s.players[1].points)
    }
}