import mcts.Action
import model.Phase
import kotlin.test.Test
import kotlin.test.assertTrue

class BriscolaMctsTest {
    @Test
    fun randomVsRandom() {
        val winRate = simulateIterations(10000,
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateRandomMove
        )
        val delta = winRate - 0.5  // should be close to 0.5
        assertTrue("Rand VS Rand = $winRate") { delta < 0.05 }
    }

    @Test
    fun ruleVsRule() {
        val winRate = simulateIterations(10000,
            BriscolaMcts.Companion::calculateRuleBasedMove,
            BriscolaMcts.Companion::calculateRuleBasedMove
        )
        val delta = winRate - 0.5  // should be close to 0.5
        assertTrue("Rule VS Rule = $winRate") { delta < 0.05 }
    }

    @Test
    fun mctsVsMcts() {
        val winRate = simulateIterations(100,
            BriscolaMcts.Companion::calculateMctsMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        val delta = winRate - 0.5  // should be close to 0.5
        assertTrue("Rand VS Rand = $winRate") { delta < 0.05 }
    }

    @Test
    fun randomVsRuleBased() {
        val winRate = simulateIterations(10000,
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateRuleBasedMove
        )
        // rule based should win more
        assertTrue("Rand VS Rule = $winRate") { winRate > 0.6 }
    }

    @Test
    fun randomVsMcts() {
        val winRate = simulateIterations(100, // less because it takes long
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        // rule based should win more
        assertTrue("Rand VS Mcts = $winRate") { winRate > 0.7 }
    }

    @Test
    fun ruleVsMcts() {
        val winRate = simulateIterations(100, // less because it takes long
            BriscolaMcts.Companion::calculateRuleBasedMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        // rule based should win more
        assertTrue("Rule VS Mcts = $winRate") { winRate > 0.7 }
    }

    private fun simulateIterations(
        iterations: Int,
        player1: (state: Briscola) -> Action<Briscola>,
        player2: (state: Briscola) -> Action<Briscola>
    ): Double {
        var wins = 0
        // todo: parallelize this
        for (i in 0 until iterations) {
            val r = simulate(i, player1, player2)
            if (r[1] >= r[0]) wins++
        }
        return wins / iterations.toDouble()
    }

    private fun simulate(
        seed: Int,
        player1: (state: Briscola) -> Action<Briscola>,
        player2: (state: Briscola) -> Action<Briscola>
    ): List<Int> {
        val state = Briscola(2)
        state.setup(seed)
        while (state.phase == Phase.PLAY) {
            if (state.player == 0) {
                player1(state).execute(state)
            } else {
                player2(state).execute(state)
            }
            state.scoreAndTake()
        }
        return state.teamScores()
    }
}