package mcts

import model.Copyable

interface Scoring<State> where State : Copyable<State> {
    fun score(state: State): Float
}