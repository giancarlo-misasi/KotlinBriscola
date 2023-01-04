package mcts

import model.Copyable

interface TerminationCheck<State> where State : Copyable<State> {
    fun isTerminal(state: State): Boolean
}