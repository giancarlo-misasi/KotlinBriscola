package mcts

import model.Copyable
import kotlin.math.ln
import kotlin.math.sqrt

class Node<ACTION, STATE>(
    val id: UInt,
    val data: STATE,
    val parent: Node<ACTION, STATE>?,
    val action: ACTION,
    createExpansionStrategy: () -> ExpansionStrategy<ACTION, STATE>,
) where STATE : Copyable<STATE>, ACTION : Action<STATE> {
    val children = mutableListOf<Node<ACTION, STATE>>()
    var numVisits = 0

    private val expansionStrategy = createExpansionStrategy()
    private var scoreSum = 0f

    val exploitationScore: Float get() = scoreSum / numVisits
    val explorationScore: Float get() = if (parent == null) 0f else sqrt(ln(parent.numVisits.toDouble()) / numVisits).toFloat()

    fun getUctScore(c: Float) = exploitationScore + c * explorationScore
    fun generateNextAction(): ACTION = expansionStrategy.generateNext(data)
    fun addChild(child: Node<ACTION, STATE>) = children.add(child)
    fun shouldExpand() = children.isEmpty() || expansionStrategy.canGenerateNext(data)

    fun update(score: Float) {
        scoreSum += score
        numVisits++
    }
}