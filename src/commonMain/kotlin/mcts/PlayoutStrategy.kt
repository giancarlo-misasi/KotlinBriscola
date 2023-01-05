package mcts

import model.Copyable

interface PlayoutStrategy<ACTION, STATE> where STATE : Copyable<STATE>, ACTION : Action<STATE> {
    fun generateRandom(state: STATE): ACTION
}