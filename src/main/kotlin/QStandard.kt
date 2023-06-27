import iteration.IntInternal

typealias FloatX = Float

typealias FloatXArray = FloatArray

const val FloatX_Zero: FloatX = 0.0f

const val FloatX_One: FloatX = 1.0f

fun floatXArrayOf(vararg elements: FloatX): FloatXArray = FloatXArray(elements.size) { elements[it] }

fun Int.toFloatX(): FloatX = toFloat()

fun Float.toFloatX(): FloatX = this

fun Double.toFloatX(): FloatX = toFloat()

inline fun <T> Sequence<T>.sumOfFloatX(selector: (T) -> FloatX): FloatX {
    var sum: FloatX = 0.toFloatX()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumOfFloatX(selector: (T) -> FloatX): FloatX {
    var sum: FloatX = 0.toFloatX()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun IntRange.windowed(size: Int, step: Int) = iterator {
    for (e in step(step)) {
        for (i in e until e + size) {
            yield(i)
        }
    }
}

inline fun IntRange.windowed(size: Int, step: Int, action: (Int) -> Unit) {
    for (e in step(step)) {
        for (i in e until e + size) {
            action(i)
        }
    }
}


fun IntRange.internal(sample: Int, internal: Int) = IntInternal(start, last, sample, internal).also {
    println(this@internal.last)
    println(it.last)
}
