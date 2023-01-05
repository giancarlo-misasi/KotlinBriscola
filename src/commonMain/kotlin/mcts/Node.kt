package mcts

import model.Copyable
import kotlin.math.ln
import kotlin.math.sqrt

class Node<State>(
    val id: UInt,
    val data: State,
    val parent: Node<State>?,
    val action: Action<State>,
    createExpansionStrategy: () -> ExpansionStrategy<State>,
) where State : Copyable<State> {
    val children = mutableListOf<Node<State>>()
    var numVisits = 0

    private val expansionStrategy = createExpansionStrategy()
    private var scoreSum = 0f

    val exploitationScore: Float get() = scoreSum / numVisits
    val explorationScore: Float get() = if (parent == null) 0f else sqrt(ln(parent.numVisits.toDouble()) / numVisits).toFloat()

    fun getUctScore(c: Float) = exploitationScore + c * explorationScore
    fun generateNextAction(): Action<State> = expansionStrategy.generateNext(data)
    fun addChild(child: Node<State>) = children.add(child)
    fun shouldExpand() = children.isEmpty() || expansionStrategy.canGenerateNext(data)

    fun update(score: Float) {
        scoreSum += score
        numVisits++
    }
}