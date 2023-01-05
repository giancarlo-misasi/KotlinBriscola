package mcts

import model.Copyable

interface ExpansionStrategy<ACTION, STATE> where STATE : Copyable<STATE>, ACTION : Action<STATE> {
    fun generateNext(state: STATE): ACTION
    fun canGenerateNext(state: STATE): Boolean
}