import model.Phase
import kotlin.test.Test
import kotlin.test.assertTrue

class BriscolaMctsTest {
    @Test
    fun playGame() {
        val teamScores = mutableListOf(0, 0)
        var wins = 0
        for (i in 0 until 100) {
            val r = simulate(i)
            teamScores[0] += r[0]
            teamScores[1] += r[1]
            if (r[1] >= r[0]) wins++
        }
        val winPercentage = wins / 100.0

        println(teamScores)
        println(wins)
        println(winPercentage)

        assertTrue { teamScores[1] > teamScores[0] }
        assertTrue { winPercentage > 0.7  }
    }

    private fun simulate(seed: Int): List<Int> {
        val state = Briscola(2)
        state.setup(seed)
        while (state.phase == Phase.PLAY) {
            if (state.player == 0) {
                val action = BriscolaMcts.calculateRandomMove(state)
                action.execute(state)
            } else {
                val action = BriscolaMcts.calculateBestMove(state)
                action.execute(state)
            }
            state.scoreAndTake()
        }
        return state.teamScores()
    }
}