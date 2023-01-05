package mcts

import model.Copyable

interface Action<STATE> where STATE : Copyable<STATE> {
    fun execute(state: STATE)
}