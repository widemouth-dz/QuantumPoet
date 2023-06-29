typealias FloatX = Float

typealias FloatXArray = FloatArray

const val FloatX_Zero: FloatX = 0.0f

const val FloatX_One: FloatX = 1.0f

fun floatXArrayOf(vararg elements: FloatX): FloatXArray = FloatXArray(elements.size) { elements[it] }

fun Int.toFloatX(): FloatX = toFloat()

fun Float.toFloatX(): FloatX = this

fun Double.toFloatX(): FloatX = toFloat()
