package org.spectral.asm.analysis

import org.spectral.asm.core.Node

/**
 * Represents an analyzer processor of some [Node] subject.
 *
 * @param T : Node
 */
interface Analyzer<T : Node> {

    /**
     * Run the analysis on a given subject instance.
     *
     * @param subject T
     */
    fun analyze(subject: T)
}