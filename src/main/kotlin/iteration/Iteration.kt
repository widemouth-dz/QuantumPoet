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

fun <T> Iterator<T>.interval(size: Int, interval: Int) = iterator {
    var sizeCount = 0
    var intervalCount = 0
    for (e in this@interval) {
        if (sizeCount++ < size) {
            yield(e)
            sizeCount++
        } else if (intervalCount++ == interval) {
            sizeCount = 0
            intervalCount = 0
        }
    }
}

fun <T> Iterable<T>.interval(size: Int, interval: Int) = iterator().interval(size, interval)
fun <T> Sequence<T>.interval(size: Int, interval: Int) = Sequence { iterator().interval(size, interval) }