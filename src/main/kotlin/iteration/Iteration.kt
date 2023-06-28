package iteration

/**
 * The fast iteration for transversal with interval.
 */
inline fun IntRange.forEachWithInterval(sample: Int, interval: Int, drop: Int = 0, action: (Int) -> Unit) {

    val safeIterator = (first + drop)..(last + 1 - sample) step (sample + interval)

    for (windowStart in safeIterator) {
        for (i in windowStart until windowStart + sample) {
            action(i)
        }
    }

    for (tail in (safeIterator.last + sample + interval)..last) {
        action(tail)
    }
}

fun IntRange.drop(drop: Int) = first + drop..last

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