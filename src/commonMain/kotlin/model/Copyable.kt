package model

interface Copyable<T> {
    fun copy(): T
}