package iteration

class IntInternal(start: Int, endInclusive: Int, val sample: Int, val internal: Int) : Iterable<Int> {

    val first: Int = start

    val last: Int = getInternalLastElement(start, endInclusive, sample, internal)

    private fun getInternalLastElement(start: Int, end: Int, sample: Int, internal: Int): Int = when {
        internal > 0 -> if (start >= end) end else
            end - (differenceModulo(end, start, sample + internal) - sample).coerceAtLeast(0)

        internal < 0 -> if (start <= end) end else
            end + (differenceModulo(start, end, sample - internal) - sample).coerceAtLeast(0)

        else -> throw kotlin.IllegalArgumentException("internal is zero.")
    }

    // (a - b) mod c
    private fun differenceModulo(a: Int, b: Int, c: Int): Int {
        return mod(mod(a, c) - mod(b, c), c)
    }

    private fun mod(a: Int, b: Int): Int {
        val mod = a % b
        return if (mod >= 0) mod else mod + b
    }

    override fun iterator(): Iterator<Int> = IntInternalIterator(first, last, sample, internal)

}

class IntInternalIterator(first: Int, last: Int, val sample: Int, val internal: Int) : IntIterator() {
    private val finalElement: Int = last
    private var hasNext: Boolean = if (internal > 0) first <= last else first >= last
    private var next: Int = if (hasNext) first else finalElement
    private var sampleCount: Int = 0

    override fun hasNext(): Boolean = hasNext

    override fun nextInt(): Int {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        } else if (sampleCount < sample) {
            sampleCount++
            next++
        } else {
            sampleCount = 0
            next += internal
        }
        return value
    }
}