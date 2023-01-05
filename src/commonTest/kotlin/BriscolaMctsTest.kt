import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class BriscolaMctsTest {

    @Test
    fun randomVsRandom() {
        val p1winRate = Briscola.simulateMany(1_000_000,
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateRandomMove
        )
        val delta = abs(p1winRate - 0.5)  // should be close to 0.5
        assertTrue("Rand VS Rand = $p1winRate") { delta < 0.05 }
    }

    @Test
    fun ruleVsRule() {
        val p1winRate = Briscola.simulateMany(1_000_000,
            BriscolaMcts.Companion::calculateRuleBasedMove,
            BriscolaMcts.Companion::calculateRuleBasedMove
        )
        val delta = abs(p1winRate - 0.5)  // should be close to 0.5
        assertTrue("Rule VS Rule = $p1winRate") { delta < 0.05 }
    }

    @Test
    fun mctsVsMcts() {
        val p1winRate = Briscola.simulateMany(100,
            BriscolaMcts.Companion::calculateMctsMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        val delta = abs(p1winRate - 0.5) // too long to simulate enough to get smaller delta
        assertTrue("Mcts VS Mcts = $p1winRate") { delta < 0.20 }
    }

    @Test
    fun randomVsRuleBased() {
        val p1winRate = Briscola.simulateMany(1_000_000,
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateRuleBasedMove
        )
        // rule based should win more
        assertTrue("Rand VS Rule = $p1winRate") { p1winRate < 0.2 }
    }

    @Test
    fun randomVsMcts() {
        val p1winRate = Briscola.simulateMany(100, // less because it takes long
            BriscolaMcts.Companion::calculateRandomMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        // mcts should win more
        assertTrue("Rand VS Mcts = $p1winRate") { p1winRate < 0.1 }
    }

    @Test
    fun ruleVsMcts() {
        val p1winRate = Briscola.simulateMany(100, // less because it takes long
            BriscolaMcts.Companion::calculateRuleBasedMove,
            BriscolaMcts.Companion::calculateMctsMove
        )
        // rule based should win more
        assertTrue("Rule VS Mcts = $p1winRate") { p1winRate < 0.35 }
    }
}