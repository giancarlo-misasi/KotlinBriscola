package mcts

import model.Copyable

interface Scoring<STATE> where STATE : Copyable<STATE> {
    fun score(state: STATE): Float
}