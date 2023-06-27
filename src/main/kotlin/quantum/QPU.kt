package quantum

import FloatX
import FloatX_One
import FloatX_Zero
import iteration.forEachWithInterval
import sumOfFloatX
import toFloatX
import java.lang.IllegalStateException
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis


class QPU internal constructor(val qBitCount: Int) {


    private val mStateCount = 1 shl qBitCount

    val mQStateTermMap = QStateTermMap(mStateCount)

    private val mAllMask: Int = mStateCount - 1

    private val Int.oneBitSequence
        get() = (0 until Int.SIZE_BITS).asSequence().map { 1 shl it }.filter { it and this != 0 }

    private val mQStateRange = (0 until mStateCount)

    private val mQStateSequence = mQStateRange.asSequence()

    /**
     * The mantissa takes 24 bits with 1 implicit bit and 23 physics bits in IEEE 754 floating-point "single format" bit layout.
     * It does not work with more than 24 bits, here is an error case to use `Float`.
     * ```
     *      2^25                  2^26
     *       ∑  (1/2^25) = 0.5     ∑  (1/2^26) = 0.25
     *       1                     1
     * ```
     */
    val totalProbability get():FloatX = mQStateTermMap.sumOf { it.vector.modulusSquare.toDouble() }.toFloatX()

    fun write(targetValue: Int, targetQBits: Int = mAllMask) {
        // It doesn't work if all bits are cleared.
        // collapse(targetValue, targetMask)
        // normalize(totalProbability())

        // Write via `read + not`.
        not(targetValue xor read(targetQBits))
    }

    fun read(targetQBits: Int = mAllMask): Int {
        return targetQBits.oneBitSequence.fold(0) { acc, qBit -> acc or readQBit(qBit) }
    }

    private fun readQBit(targetQBit: Int): Int {
        var result = 0
        val possibility:FloatX
        measureTimeMillis { possibility = peekQBitProbability(targetQBit) }.also { println("peekQBitProbability takes : $it") }
        val random = Random.nextDouble()
        // The random is in [0,1).
        require(random >= 0 && random < 1) { throw IllegalStateException("Illegal random!") }
        val totalProbability = if (random < possibility) {
            result = targetQBit
            possibility
        } else {
            1 - possibility
        }
        require(totalProbability > 0) { throw IllegalStateException("The inevitable event did not happen!") }
        measureTimeMillis { collapse(targetQBit, result) }.also { println("collapse takes : $it") }
        measureTimeMillis { normalize(totalProbability) }.also { println("normalize takes : $it") }
        return result
    }

    private fun normalize(totalProbability: FloatX) {
        when (totalProbability) {
            0.toFloatX() -> initialize(0)
            1.toFloatX() -> Unit
            else -> scale(1 / sqrt(totalProbability))
        }
    }

    /** All quantum bits collapse to classic bits, and the result is [value]. */
    private fun initialize(value: Int) {
        mQStateTermMap.forEach { it.vector.set(FloatX_Zero, FloatX_One) }
        mQStateTermMap[value].vector.real = FloatX_One
    }

    private fun scale(scale: FloatX) {
        mQStateTermMap.forEach { it.vector.set(it.vector * scale) }
    }

    /**
     * Quantum bits collapse to classic bits, any quantum states disobey it will be cleared.
     * @param targetValue The classic bits to collapse to
     * @param targetQBits  The bits to collapse.
     */
    private fun collapse(targetValue: Int, targetQBits: Int) {
        targetQBits.oneBitSequence
            .map { targetQBit ->
                // the quantum bit collapses to `0` -> clear [it*(2n+1) until it*2n]
                // the quantum bit collapses to `1` -> clear [it*2n until it*(2n+1)]
                // equivalent to: if (targetQBit and targetValue == 0) targetQBit else 0
                val clearOffset = targetQBit and targetValue xor targetQBit
                mQStateSequence.drop(clearOffset).windowed(targetQBit, targetQBit * 2, true)
            }
            .flatMap { windowedSequence -> windowedSequence.flatMap { it } }
            .forEach { mQStateTermMap[it].vector.set(FloatX_Zero, FloatX_Zero) }
    }

    private fun peekQBitProbability(targetQBit: Int) =
        mQStateSequence
            .drop(targetQBit) // Skip values smaller than `singleBit`.
            .windowed(targetQBit, targetQBit * 2, true)
            .flatMap { it }
            .sumOf { mQStateTermMap[it].vector.modulusSquare.toDouble() }
            .toFloatX()


    fun conditionHadamard(targetQBits: Int, conditionMask: Int) {
        targetQBits.oneBitSequence.forEach { conditionHadamardQBit(it, conditionMask) }
    }

    private fun conditionHadamardQBit(targetQBit: Int, conditionMask: Int) {
        if (targetQBit and conditionMask != 0) return

        val filterMark = targetQBit or conditionMask
        (conditionMask until mStateCount)
            .asSequence()
            .filter { it and filterMark == conditionMask }
            .forEach {
                val state0 = mQStateTermMap[it].vector
                val state1 = mQStateTermMap[it + targetQBit].vector
                mQStateTermMap[it].vector.set((state0 + state1) * sOneOverRoot2)
                mQStateTermMap[it + targetQBit].vector.set((state0 - state1) * sOneOverRoot2)
            }
    }

    fun hadamard(targetQBits: Int = mAllMask) {
        targetQBits.oneBitSequence.forEach { hadamardQBit(it) }
    }

    private fun hadamardQBit(targetQBit: Int) {
        mQStateRange.forEachWithInterval(targetQBit, targetQBit) {
            val state0 = mQStateTermMap[it]
            val state1 = mQStateTermMap[it + targetQBit]

            val vector0 = (state0.vector + state1.vector) * sOneOverRoot2
            val vector1 = (state0.vector - state1.vector) * sOneOverRoot2

            state0.vector.set(vector0)
            state1.vector.set(vector1)

            mQStateTermMap[it] = state0
            mQStateTermMap[it + targetQBit] = state1
        }
    }

    fun not(targetQBits: Int = mAllMask) {
        measureTimeMillis { targetQBits.oneBitSequence.forEach { notQBit(it) } }.also { println("not takes : $it") }
    }

    private fun notQBit(targetQBit: Int) {
        mQStateRange.forEachWithInterval(targetQBit, targetQBit) {
            // Exchange the quantum pair.
            val temp = mQStateTermMap[it]
            mQStateTermMap[it] = mQStateTermMap[it + targetQBit]
            mQStateTermMap[it + targetQBit] = temp
        }
    }

    fun conditionNot(targetQBits: Int, conditionMask: Int) {
        targetQBits.oneBitSequence.forEach { conditionNotQBit(it, conditionMask) }
    }

    private fun conditionNotQBit(targetQBit: Int, conditionMask: Int) {
        val filterMark = targetQBit or conditionMask
        // If quantum state is meet with the condition, it must be at least greater than conditionMask.
        (conditionMask until mStateCount)
            .asSequence()
            // We only want to get the first of the quantum pair, it meets that x & (targetMask|conditionMask) == conditionMask.
            .filter { it and filterMark == conditionMask }
            .forEach {
                // Exchange the quantum pair.
                val temp = mQStateTermMap[it]
                mQStateTermMap[it] = mQStateTermMap[it + targetQBit]
                mQStateTermMap[it + targetQBit] = temp
            }
    }


    companion object {
        val sOneOverRoot2 = 1 / sqrt(2.0).toFloatX()
    }
}