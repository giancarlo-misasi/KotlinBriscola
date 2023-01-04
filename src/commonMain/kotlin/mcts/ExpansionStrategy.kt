package mcts

import model.Copyable

interface ExpansionStrategy<State> where State : Copyable<State> {
    fun generateNext(state: State): Action<State>
    fun canGenerateNext(state: State): Boolean
}