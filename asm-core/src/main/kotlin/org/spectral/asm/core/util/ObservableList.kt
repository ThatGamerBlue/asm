package org.spectral.asm.core.util

class ObservableList<T>(private val wrapped: MutableList<T>, private val onChanged: (T) -> Unit): MutableList<T> by wrapped {

    override fun add(element: T): Boolean {
        if(wrapped.add(element)) {
            onChanged(element)
            return true
        }
        return false
    }

    override fun remove(element: T): Boolean {
        if(wrapped.remove(element)) {
            onChanged(element)
            return true
        }
        return false
    }
}