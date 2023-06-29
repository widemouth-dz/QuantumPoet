import java.lang.IllegalStateException
import kotlin.math.*
import kotlin.random.Random


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
        val possibility = peekQBitProbability(targetQBit)
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
        collapse(targetQBit, result)
        normalize(totalProbability)
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

        mQStateTermMap.forEachIndexed { index, qStateTerm ->
            mQStateTermMap[index] = qStateTerm.apply { vector.set(FloatX_Zero, FloatX_One) }
        }

        mQStateTermMap[value] = mQStateTermMap[value].apply { vector.real = FloatX_One }
    }

    private fun scale(scale: FloatX) {
        mQStateTermMap.forEachIndexed { index, qStateTerm ->
            mQStateTermMap[index] = qStateTerm.apply { vector.set(qStateTerm.vector * scale) }
        }
    }

    /**
     * Quantum bits collapse to classic bits, any quantum states disobey it will be cleared.
     * @param targetValue The classic bits to collapse to
     * @param targetQBits  The bits to collapse.
     */
    private fun collapse(targetQBits: Int, targetValue: Int) {
        when (targetQBits.countOneBits()) {
            0 -> Unit
            1 -> {
                val clearOffset = targetQBits and targetValue xor targetQBits
                mQStateRange.drop(clearOffset).forEachWithInterval(sample = targetQBits, interval = targetQBits) {
                    mQStateTermMap[it] = mQStateTermMap[it].apply { vector.set(FloatX_Zero, FloatX_Zero) }
                }
            }
            // For 2 case: we can iterate with `1` case twice, the count of loops is equal to the `else` case,
            // and without `if` action, but the actual performance is not better, because it is inferior in cache.
            else -> mQStateRange.forEach {
                if (it and targetQBits != targetValue)
                    mQStateTermMap[it] = mQStateTermMap[it].apply { vector.set(FloatX_Zero, FloatX_Zero) }
            }
        }
    }

    private fun peekQBitProbability(targetQBit: Int): FloatX {
        var sum = 0.0
        mQStateRange.drop(targetQBit).forEachWithInterval(sample = targetQBit, interval = targetQBit) {
            sum += mQStateTermMap[it].vector.modulusSquare.toDouble()
        }
        return sum.toFloatX()
    }


    fun conditionHadamard(targetQBits: Int, conditionMask: Int) {
        targetQBits.oneBitSequence.forEach { conditionHadamardQBit(it, conditionMask) }
    }

    private fun conditionHadamardQBit(targetQBit: Int, conditionMask: Int = 0) {
        if (targetQBit and conditionMask != 0) return

        fun hadamardPair(qBit0: Int, qBit1: Int) {

            val state0 = mQStateTermMap[qBit0]
            val state1 = mQStateTermMap[qBit1]

            val vector0 = (state0.vector + state1.vector) * sOneOverRoot2
            val vector1 = (state0.vector - state1.vector) * sOneOverRoot2

            state0.vector.set(vector0)
            state1.vector.set(vector1)

            mQStateTermMap[qBit0] = state0
            mQStateTermMap[qBit1] = state1

        }

        val filterMark = targetQBit or conditionMask

        when (conditionMask.countOneBits()) {
            0 -> mQStateRange.forEachWithInterval(sample = targetQBit, interval = targetQBit) {
                hadamardPair(it, it + targetQBit)
            }

            1 -> mQStateRange.drop(conditionMask)
                .forEachWithInterval(sample = conditionMask, interval = conditionMask) {
                    if (it and filterMark == conditionMask) hadamardPair(it, it + targetQBit)
                }

            else -> mQStateRange.drop(conditionMask).forEach {
                if (it and filterMark == conditionMask) hadamardPair(it, it + targetQBit)
            }

        }
    }

    fun hadamard(targetQBits: Int = mAllMask) {
        targetQBits.oneBitSequence.forEach { conditionHadamardQBit(it) }
    }

    fun not(targetQBits: Int = mAllMask) {
        targetQBits.oneBitSequence.forEach { conditionNotQBit(it) }
    }

    fun conditionNot(targetQBits: Int, conditionMask: Int) {
        targetQBits.oneBitSequence.forEach { conditionNotQBit(it, conditionMask) }
    }

    private fun conditionNotQBit(targetQBit: Int, conditionMask: Int = 0) {

        fun exchangePair(qBit0: Int, qBit1: Int) {
            val temp = mQStateTermMap[qBit0]
            mQStateTermMap[qBit0] = mQStateTermMap[qBit1]
            mQStateTermMap[qBit1] = temp
        }

        val filterMark = targetQBit or conditionMask
        when (conditionMask.countOneBits()) {
            0 -> mQStateRange.forEachWithInterval(sample = targetQBit, interval = targetQBit) {
                exchangePair(it, it + targetQBit)
            }

            1 -> mQStateRange.drop(conditionMask)
                .forEachWithInterval(sample = conditionMask, interval = conditionMask) {
                    if (it and filterMark == conditionMask) exchangePair(it, it + targetQBit)
                }

            else -> mQStateRange.drop(conditionMask).forEach {
                if (it and filterMark == conditionMask) exchangePair(it, it + targetQBit)
            }

        }
    }

    fun phaseShift(degreeTheta: FloatX, targetQBits: Int = mAllMask, conditionQuBits: Int = 0) {
        when (targetQBits.countOneBits()) {
            0 -> Unit
            1 -> conditionPhaseShift(degreeTheta, targetQBits or conditionQuBits)
            else -> targetQBits.oneBitSequence.forEach { conditionPhaseShift(degreeTheta, it or conditionQuBits) }
        }
    }

    fun conditionPhaseShift(degreeTheta: FloatX, conditionQuBits: Int = mAllMask) {

        val radian = degreeTheta * sDegreeToRadianFactor
        val sinTheta = sin(radian)
        val cosTheta = cos(radian)

        /**
         * ```
         * x = r·cos(α)
         * y = r·sin(α)
         * x` = r·cos(α+θ) = r·cos(α)·cos(θ) + r·sin(α)·sin(θ) = x·cos(θ) - y·sin(θ)
         * y` = r·sin(α+θ) = r·sin(α)·cos(θ) + r·cos(α)·sin(θ) = y·cos(θ) + x·sin(θ)
         * ```
         */
        fun phaseShiftTerm(targetTerm: Int) {
            val qStateTerm = mQStateTermMap[targetTerm]
            val real = qStateTerm.vector.real
            val image = qStateTerm.vector.image
            qStateTerm.vector.real = real * cosTheta - image * sinTheta
            qStateTerm.vector.image = image * cosTheta + real * sinTheta
            mQStateTermMap[targetTerm] = qStateTerm
        }

        when (conditionQuBits.countOneBits()) {
            0 -> mQStateRange.forEach { phaseShiftTerm(it) }
            1 -> mQStateRange.drop(conditionQuBits)
                .forEachWithInterval(sample = conditionQuBits, interval = conditionQuBits) { phaseShiftTerm(it) }

            else -> mQStateRange.forEach {
                if (it and conditionQuBits == conditionQuBits) phaseShiftTerm(it)
            }
        }
    }


    companion object {
        val sDegreeToRadianFactor = (PI / 180).toFloatX()
        val sOneOverRoot2 = 1 / sqrt(2.0).toFloatX()
    }
}