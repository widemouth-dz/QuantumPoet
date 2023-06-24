package quantum

import Complex
import kotlin.math.sqrt
import kotlin.random.Random

class QPU internal constructor(private val count: Int) {

    private val mStateCount = 1 shl count

    val mQStates = Array(mStateCount) { QState(Complex(0.0, 0.0)) }.apply {
        this[0] = QState(Complex(1.0, 0.0))
    }

    fun read(targetMask: Int) {
        var result = 0
        (0 until count)
            .asSequence()
            .map { 1 shl it }
            .filter { it and targetMask != 0 }
            .forEach {
                val possibility = possibility(it)
                val random = Random.nextDouble()
                val chaosValue =
                    if (random <= possibility) {
                        result = result or it
                        possibility
                    } else {
                        1 - possibility
                    }
                collapse(targetMask, result)
                normalize(chaosValue)
            }
    }

    fun normalize(chaosValue: Double) {
        scale(1 / sqrt(chaosValue))
    }

    fun scale(scale: Double) {
        mQStates.forEach { it.complex *= scale }
    }

    fun collapse(targetMask: Int, targetValue: Int) {
        (0 until count)
            .asSequence()
            .map { 1 shl it }
            .filter { it and targetMask != 0 }
            .forEach { singleBit ->
                // the quantum bit collapses to `0` -> clear [it*(2n+1) until it*2n]
                // the quantum bit collapses to `1` -> clear [it*2n until it*(2n+1)]
                var clearStarter = if (singleBit and targetValue == 0) singleBit else 0
                repeat(mStateCount / singleBit / 2) {
                    repeat(singleBit) {
                        mQStates[clearStarter + it].complex.set(0.0, 0.0)
                    }
                    clearStarter += singleBit
                }
            }
    }

    fun possibility(targetMask: Int) =
        (targetMask until (1 shl count))
            .asSequence()
            .filter { it and targetMask == targetMask }
            .sumOf { mQStates[it].complex.modulusSquare }


    fun conditionHadamard(targetMask: Int, conditionMask: Int) {
        (0 until count)
            .asSequence()
            .map { 1 shl it }
            .filter { it and targetMask != 0 }
            .forEach { conditionHadamardSingleBit(it, conditionMask) }
    }

    fun conditionHadamardSingleBit(target: Int, conditionMask: Int) {
        if (target and conditionMask != 0) return

        val oneOverRoot2 = 1 / sqrt(2.0)

        val filterMark = target or conditionMask
        (conditionMask until (1 shl count))
            .asSequence()
            .filter { it and filterMark == conditionMask }
            .forEach {
                val state0 = mQStates[it].complex
                val state1 = mQStates[it + target].complex
                mQStates[it].complex = (state0 + state1) * oneOverRoot2
                mQStates[it + target].complex = (state0 - state1) * oneOverRoot2
            }
    }

    fun conditionNot(targetMask: Int, conditionMask: Int) {
        val filterMark = targetMask or conditionMask
        // If quantum state is meet with the condition, it must be at least greater than conditionMask.
        (conditionMask until (1 shl count))
            .asSequence()
            // We only want to get the first of the quantum pair, which is x & (targetMask|conditionMask) == conditionMask.
            .filter { it and filterMark == conditionMask }
            .forEach {
                // Exchange the quantum pair.
                val temp = mQStates[it]
                mQStates[it] = mQStates[it + targetMask]
                mQStates[it + targetMask] = temp
            }
    }

    class QState(var complex: Complex) {
        override fun toString(): String = "QState[complex = $complex]"
    }
}