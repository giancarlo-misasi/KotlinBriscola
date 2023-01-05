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
        val briscola = Briscola(2)
        briscola.setup(26)

        while (briscola.phase == Phase.PLAY) {
            briscola.play(briscola.currentPlayer().cards().first())
            briscola.play(briscola.currentPlayer().cards().first())
            briscola.scoreAndTake()
        }

        assertEquals(briscola.phase, Phase.END)
        assertEquals(briscola.players[0].points, 39)
        assertEquals(briscola.players[1].points, 81)
    }
}