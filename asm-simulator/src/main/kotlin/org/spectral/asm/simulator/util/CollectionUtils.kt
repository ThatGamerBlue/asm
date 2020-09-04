package org.spectral.asm.simulator.util

fun <T> combine(src: List<T>, additional: T): List<T> {
    val list = src.toMutableList()
    list.add(additional)
    return list
}

fun <T> combine(src: List<T>, additional: List<T>): List<T> {
    val list = src.toMutableList()
    list.addAll(additional)
    return list
}