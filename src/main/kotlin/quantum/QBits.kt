package quantum

import Complex

open class QBits internal constructor(private val bits: List<QPU.QBit>) : List<QPU.QBit> by bits


class QPU internal constructor(
    private val count: Int,
    private val delegate: MutableList<QBit> = ArrayList(count)
) : QBits(delegate) {

    init {
        repeat(count) { delegate.add(it, QBit(it)) }
    }

    private val mQStates = Array(1 shl size) { QState(Complex(0.0, 0.0)) }


    inner class QBit(private val index: Int) {
        fun not() {

        }
    }

    inner class QInt internal constructor(bits: List<QPU.QBit>) : QBits(bits) {

        fun cnot(targetMask: Int, conditionMask: Int) {
            val filterMark = targetMask or conditionMask
            // If quantum state is meet with the condition, it must be at least greater than conditionMask.
            (conditionMask..(1 shl size))
                .asSequence()
                // We only want to get the first of the quantum pair, which is x & (targetMask|conditionMask) == conditionMask.
                .filter { it and filterMark == conditionMask }
                .forEach {
                    // Exchange the quantum pair.
                    val temp = mQStates[it]
                    mQStates[it] = mQStates[it + targetMask]
                    mQStates[it] = temp
                }
        }

    }
}

fun qPU(count: Int) = QPU(3)

class QState(complex: Complex)

val draft = QBits(emptyList()).apply {
}