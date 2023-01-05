package mcts

import model.Copyable
import java.io.File
import kotlin.random.Random

class Mcts<ACTION, STATE>(
    rootData: STATE,
    action: ACTION,
    private val backprop: Backpropagation<STATE>,
    private val termination: TerminationCheck<STATE>,
    private val playout: PlayoutStrategy<ACTION, STATE>,
    private val scoring: Scoring<STATE>,
    private val createExpansionStrategy: () -> ExpansionStrategy<ACTION, STATE>,
    private val generator: Random = Random(System.currentTimeMillis()),
) where STATE : Copyable<STATE>, ACTION : Action<STATE> {
    private val root = Node(0u, rootData, null, action, createExpansionStrategy)
    private var currentNodeID = 0u
    private var iterations = 0

    var allowedComputationTime = DEFAULT_TIME
    var minIterations = DEFAULT_MIN_ITERATIONS
    var c = DEFAULT_C
    var minT = DEFAULT_MIN_T
    var minVisits = DEFAULT_MIN_VISITS

    fun calculateAction(): ACTION {
        search()

        val best = root.children.maxByOrNull { it.exploitationScore }
            ?: return playout.generateRandom(root.data.copy())

        if (DEBUG) File("BriscolaGraph.dot").writeText(toGraphvizDotFileString())
        return best.action
    }

    fun toGraphvizDotFileString(): String {
        val worklist = mutableListOf(root)
        return buildString {
            append("digraph MCTS {\n")
            while (!worklist.isEmpty()) {
                val current = worklist.removeFirst()
                append("${current.id} [label=\"${current.data}\nVisits: ${current.numVisits}\nScore: ${current.exploitationScore}\"];\n")
                if (current.id != root.id) {
                    append("${current.parent?.id} -> ${current.id} [label=\"${current.action}\"]\n")
                }
                worklist.addAll(current.children)
            }
            append("}\n")
        }
    }

    private fun search() {
        val time = System.currentTimeMillis()
        while ((System.currentTimeMillis() - time) < allowedComputationTime || iterations < minIterations) {
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

    private fun select(node: Node<ACTION, STATE>): Node<ACTION, STATE> {
        // Select randomly if we don't have enough visits
        if (node.numVisits < minVisits) {
            // should have at least 1 child
            return node.children[generator.nextInt(node.children.size)]
        }

        // Otherwise use the UCT formula
        return node.children.maxBy { it.getUctScore(c) }
    }

    private fun backprop(node: Node<ACTION, STATE>, score: Float) {
        node.update(backprop.updateScore(node.data, score))

        var current = node.parent
        while (current != null) {
            current.update(backprop.updateScore(current.data, score))
            current = current.parent
        }
    }

    private fun expandNext(node: Node<ACTION, STATE>): Node<ACTION, STATE> {
        val data = node.data.copy()
        val action = node.generateNextAction()
        action.execute(data)

        val newNode = Node(++currentNodeID, data, node, action, createExpansionStrategy)
        node.addChild(newNode)
        return newNode
    }

    private fun simulate(node: Node<ACTION, STATE>) {
        val state = node.data.copy()
        while (!termination.isTerminal(state)) {
            val action = playout.generateRandom(state)
            action.execute(state)
        }

        backprop(node, scoring.score(state))
    }

    companion object {
        const val DEBUG = true
        const val DEFAULT_TIME = 500
        const val DEFAULT_MIN_ITERATIONS = 0
        const val DEFAULT_C = 0.5f
        const val DEFAULT_MIN_T = 5
        const val DEFAULT_MIN_VISITS = 5
    }
}