package mcts

import model.Copyable
import kotlin.random.Random

class Mcts<State>(
    rootData: State,
    private val action: Action<State>,
    private val backprop: Backpropagation<State>,
    private val termination: TerminationCheck<State>,
    private val playout: PlayoutStrategy<State>,
    private val scoring: Scoring<State>,
    private val createExpansionStrategy: () -> ExpansionStrategy<State>,
    private val generator: Random = Random(System.currentTimeMillis()),
) where State : Copyable<State> {
    private val root = Node(0u, rootData, null, action, createExpansionStrategy)
    private var currentNodeID = 0u
    private var iterations = 0

    var allowedComputationTime = DEFAULT_TIME
    var minIterations = DEFAULT_MIN_ITERATIONS
    var c = DEFAULT_C
    var minT = DEFAULT_MIN_T
    var minVisits = DEFAULT_MIN_VISITS

    fun calculateAction(): Action<State> {
        search()

        val best = root.children.maxByOrNull { it.exploitationScore }
            ?: return playout.generateRandom(root.data.copy())

        return best.action
    }

    private fun search() {
        val time = System.currentTimeMillis()
        while (System.currentTimeMillis() - time > allowedComputationTime || iterations < minIterations) {
            iterations++

            // Selection
            var selected = root
            while (!selected.shouldExpand()) {
                selected = select(selected)
            }

            if (termination.isTerminal(selected.data)) {
                backprop(selected, scoring.score(selected.data))
                continue
            }

            // Expansion
            val numVisits = selected.numVisits
            val expanded = if (numVisits >= minT) {
                expandNext(selected)
            } else {
                selected
            }

            // Simulation
            simulate(expanded)
        }
    }

    private fun select(node: Node<State>): Node<State> {
        // Select randomly if we don't have enough visits
        if (node.numVisits < minVisits) {
            // should have at least 1 child
            return node.children[generator.nextInt(node.children.size)]
        }

        // Otherwise use the UCT formula
        return node.children.maxBy { it.getUctScore(c) }
    }

    private fun backprop(node: Node<State>, score: Float) {
        node.update(backprop.updateScore(node.data, score))

        var current = node.parent
        while (current != null) {
            current.update(backprop.updateScore(current.data, score))
            current = current.parent
        }
    }

    private fun expandNext(node: Node<State>): Node<State> {
        val data = node.data.copy()
        val action = node.generateNextAction()
        action.execute(data)

        val newNode = Node(++currentNodeID, data, node, action, createExpansionStrategy)
        node.addChild(newNode)
        return newNode
    }

    private fun simulate(node: Node<State>) {
        val state = node.data.copy()
        while (!termination.isTerminal(state)) {
            val action = playout.generateRandom(state)
            action.execute(state)
        }

        backprop(node, scoring.score(state))
    }

    companion object {
        const val DEFAULT_TIME = 500
        const val DEFAULT_MIN_ITERATIONS = 0
        const val DEFAULT_C = 0.5f
        const val DEFAULT_MIN_T = 5
        const val DEFAULT_MIN_VISITS = 5
    }
}