package mcts

import model.Copyable

interface Action<State> where State : Copyable<State> {
    fun execute(state: State)
}