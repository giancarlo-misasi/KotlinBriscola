package mcts

import model.Copyable

interface Backpropagation<State> where State : Copyable<State> {
    fun updateScore(state: State, backpropScore: Float): Float
}