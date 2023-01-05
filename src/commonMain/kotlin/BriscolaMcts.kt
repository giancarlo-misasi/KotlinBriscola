import mcts.Action
import mcts.Backpropagation
import mcts.ExpansionStrategy
import mcts.Mcts
import mcts.PlayoutStrategy
import mcts.Scoring
import mcts.TerminationCheck
import model.Card
import model.Face
import model.Phase
import model.Suit
import kotlin.random.Random

class BriscolaMcts {
    class BriscolaAction(val card: Card) : Action<Briscola> {
        override fun execute(state: Briscola) {
            if (state.play(card)) state.scoreAndTake()
        }

        override fun toString(): String = card.toString()
    }

    class BriscolaBackpropagation : Backpropagation<Briscola> {
        override fun updateScore(state: Briscola, backpropScore: Float): Float = backpropScore
    }

    class BriscolaTerminationCheck : TerminationCheck<Briscola> {
        override fun isTerminal(state: Briscola): Boolean = state.phase == Phase.END
    }

    class BriscolaPlayoutStrategy : PlayoutStrategy<Briscola> {
        override fun generateRandom(state: Briscola) = calculateRandomMove(state)
    }

    class BriscolaScoring(player: Int) : Scoring<Briscola> {
        private val team = player % 2

        override fun score(state: Briscola): Float {
            val scores = state.teamScores()
            val myScore = scores[team]
            val opponentScore = scores[if (team == 0) 1 else 0]
            return if (myScore > opponentScore) {
                1f
            } else if (myScore < opponentScore) {
                0f
            } else {
                0.5f
            }
        }
    }

    class BriscolaExpansionStrategy : ExpansionStrategy<Briscola> {
        private var index = 0

        override fun generateNext(state: Briscola): Action<Briscola> {
            val i = index++
            return BriscolaAction(state.currentPlayer().cards()[i])
        }

        override fun canGenerateNext(state: Briscola): Boolean {
            return index < state.currentPlayer().cards().size
        }
    }

    companion object {
        private val generator = Random(System.currentTimeMillis())
        private val action = BriscolaAction(Card(Suit.SPADE, Face.TWO))
        private val backprop = BriscolaBackpropagation()
        private val termination = BriscolaTerminationCheck()
        private val playout = BriscolaPlayoutStrategy()

        fun calculateRandomMove(state: Briscola): Action<Briscola> {
            val cards = state.currentPlayer().cards()
            return BriscolaAction(cards[generator.nextInt(cards.size)])
        }

        fun calculateBestMove(state: Briscola): Action<Briscola> {
            val mcts = Mcts(
                state,
                action,
                backprop,
                termination,
                playout,
                BriscolaScoring(state.player),
                { BriscolaExpansionStrategy() }
            )
            mcts.allowedComputationTime = 100
            mcts.minIterations = 20
            return mcts.calculateAction()
        }
    }
}