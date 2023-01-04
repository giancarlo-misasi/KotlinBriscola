package mcts

import model.Copyable

interface PlayoutStrategy<State> where State : Copyable<State> {
    fun generateRandom(state: State): Action<State>
}