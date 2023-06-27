package iteration

import windowed

inline fun IntRange.forEachWindowed(size: Int, step: Int, action: (Int) -> Unit) {
    val windowLeadingIterator = this step (step)

    val safeIterator =
        if (contains(windowLeadingIterator.last + size)) windowLeadingIterator else first..last + 1 - size

    for (windowStart in safeIterator) {
        for (i in windowStart until windowStart + size) {
            action(i)
        }
    }

    for (tail in windowLeadingIterator.last..last) {
        action(tail)
    }
}

inline fun IntRange.forEachWithInterval(sample: Int, interval: Int, action: (Int) -> Unit) =
    windowed(sample, sample + interval, action)

fun <T> Iterable<T>.windowed(size: Int, step: Int) = sequence<T> {
    var sizeCount = 0
    var stepCount = 0
    for (e in this@windowed) {
        if (sizeCount++ < size) {
            yield(e)
        }else{
            sizeCount++
        }
    }
}