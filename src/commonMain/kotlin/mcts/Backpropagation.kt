package mcts

import model.Copyable

interface Backpropagation<STATE> where STATE : Copyable<STATE> {
    fun updateScore(state: STATE, backpropScore: Float): Float
}