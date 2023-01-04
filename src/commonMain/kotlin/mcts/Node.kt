package mcts

import model.Copyable
import kotlin.math.ln
import kotlin.math.sqrt

class Node<State>(
    val id: UInt,
    val data: State,
    val parent: Node<State>?,
    val action: Action<State>,
    val expansion: ExpansionStrategy<State>,
) where State : Copyable<State> {
    val children = mutableListOf<Node<State>>()
    var numVisits = 0
    private var scoreSum = 0f

    fun getAvgScore() = scoreSum / numVisits
    fun generateNextAction(): Action<State> = expansion.generateNext(data)
    fun addChild(child: Node<State>) = children.add(child)
    fun shouldExpand() = children.isEmpty() || expansion.canGenerateNext(data)

    fun update(score: Float) {
        scoreSum += score
        numVisits++
    }

    fun getUctScore(parentNumVisits: Int, c: Float): Float {
        return getAvgScore() + c * sqrt(ln(parentNumVisits.toDouble()) / numVisits).toFloat()
    }
}