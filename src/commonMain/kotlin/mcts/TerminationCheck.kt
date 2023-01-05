package mcts

import model.Copyable

interface TerminationCheck<STATE> where STATE : Copyable<STATE> {
    fun isTerminal(state: STATE): Boolean
}