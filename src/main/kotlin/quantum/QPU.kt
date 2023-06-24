package quantum

import Complex
import kotlin.math.sqrt
import kotlin.random.Random

class QPU internal constructor(private val count: Int) {

    private val mStateCount = 1 shl count

    val mQStateInfos = Array(mStateCount) { QState(Complex(0.0, 0.0)) }.apply {
        this[0] = QState(Complex(1.0, 0.0))
    }

    private val mQStateSequence = (0 until mStateCount).asSequence()

    private val mQBitSequence = (0..count).asSequence().map { 1 shl it }

    fun write(targetValue: Int, targetMask: Int) {
        collapse(targetValue, targetMask)
        normalize(totalProbability())
        // read + not 相反的路
        val oppositeMask = targetValue xor read(targetMask)
        conditionNot(oppositeMask, Int.SIZE_BITS)
    }

    fun read(targetMask: Int): Int {
        var result = 0
        mQBitSequence
            .filter { it and targetMask != 0 }
            .forEach {
                val possibility = peekSingleBitProbability(it)
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
        return result
    }

    private fun normalize(totalProbability: Double) {
        scale(1 / sqrt(totalProbability))
    }

    private fun scale(scale: Double) {
        mQStateInfos.forEach { it.complex *= scale }
    }

    /**
     * Quantum bits collapse to classic bits, any quantum states disobey it will be cleared.
     * @param targetValue The classic bits to collapse to
     * @param targetMask  The bits to collapse.
     */
    private fun collapse(targetValue: Int, targetMask: Int) {
        mQBitSequence
            .filter { it and targetMask != 0 }
            .map { singleBit ->
                // the quantum bit collapses to `0` -> clear [it*(2n+1) until it*2n]
                // the quantum bit collapses to `1` -> clear [it*2n until it*(2n+1)]
                mQStateSequence
                    .drop(if (singleBit and targetValue == 0) singleBit else 0)
                    .windowed(singleBit, singleBit * 2, true)
            }
            .flatMap { windowedSequence -> windowedSequence.flatMap { it } }
            .forEach { mQStateInfos[it].complex.set(0.0, 0.0) }
    }

    private fun peekSingleBitProbability(singleBit: Int) =
        mQStateSequence
            .drop(singleBit) // Skip values smaller than `singleBit`.
            .windowed(singleBit, singleBit * 2, true)
            .flatMap { it }
            .sumOf { mQStateInfos[it].complex.modulusSquare }

    private fun totalProbability(): Double = mQStateInfos.sumOf { it.complex.modulusSquare }

    fun conditionHadamard(targetMask: Int, conditionMask: Int) {
        mQBitSequence
            .filter { it and targetMask != 0 }
            .forEach { conditionHadamardSingleBit(it, conditionMask) }
    }

    private fun conditionHadamardSingleBit(singleBit: Int, conditionMask: Int) {
        if (singleBit and conditionMask != 0) return

        val oneOverRoot2 = 1 / sqrt(2.0)

        val filterMark = singleBit or conditionMask
        (conditionMask until mStateCount)
            .asSequence()
            .filter { it and filterMark == conditionMask }
            .forEach {
                val state0 = mQStateInfos[it].complex
                val state1 = mQStateInfos[it + singleBit].complex
                mQStateInfos[it].complex = (state0 + state1) * oneOverRoot2
                mQStateInfos[it + singleBit].complex = (state0 - state1) * oneOverRoot2
            }
    }

    fun not(targetQbits: Int) {
        mQBitSequence
            .filter { it and targetQbits != 0 }
            .forEach { notQBit(it) }
    }

    private fun notQBit(targetQBit: Int) {
        mQStateSequence
            .windowed(targetQBit, targetQBit * 2, true)
            .flatMap { it }
            .forEach {
                // Exchange the quantum pair.
                val temp = mQStateInfos[it]
                mQStateInfos[it] = mQStateInfos[it + targetQBit]
                mQStateInfos[it + targetQBit] = temp
            }
    }

    fun conditionNot(targetMask: Int, conditionMask: Int) {
        mQBitSequence
            .filter { it and targetMask != 0 }
            .forEach { conditionNotQBit(it, conditionMask) }
    }

    private fun conditionNotQBit(targetQBit: Int, conditionMask: Int) {
        val filterMark = targetQBit or conditionMask
        // If quantum state is meet with the condition, it must be at least greater than conditionMask.
        (conditionMask until mStateCount)
            .asSequence()
            // We only want to get the first of the quantum pair, which is x & (targetMask|conditionMask) == conditionMask.
            .filter { it and filterMark == conditionMask }
            .forEach {
                // Exchange the quantum pair.
                val temp = mQStateInfos[it]
                mQStateInfos[it] = mQStateInfos[it + targetQBit]
                mQStateInfos[it + targetQBit] = temp
            }
    }

    class QState(var complex: Complex) {
        override fun toString(): String = "QState[complex = $complex]"
    }
}