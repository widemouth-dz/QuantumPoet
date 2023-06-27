import kotlin.math.atan2
import kotlin.math.sqrt

// typealias Complex = Pair<Double, Double>

@JvmInline
value class Complex private constructor(private val doubleArray: FloatXArray) {
    constructor(real: FloatX, image: FloatX) : this(floatXArrayOf(real, image))

    var real: FloatX
        get() = doubleArray[0]
        set(value) {
            doubleArray[0] = value
        }
    var image: FloatX
        get() = doubleArray[1]
        set(value) {
            doubleArray[1] = value
        }
    val modulusSquare get():FloatX = real * real + image * image
    val modulus get() = sqrt(modulusSquare)
    val argument get() = atan2(image, real)
    operator fun plus(other: Complex) = Complex(real.plus(other.real), image.plus(other.image))

    operator fun minus(other: Complex) = Complex(real.minus(other.real), image.minus(other.image))

    operator fun div(other: Complex): Complex {
        val denominator = other.real * other.real + other.image * other.image
        val realNumerator = real * other.real + image * other.image
        val imageNumerator = image * other.real - real * other.image
        return Complex(realNumerator / denominator, imageNumerator / denominator)

    }

    operator fun div(other: FloatX) = Complex(real.div(other), image.div(other))

    operator fun times(other: FloatX) = Complex(real.times(other), image.times(other))

    fun set(value: Complex) {
        doubleArray[0] = value.doubleArray[0]
        doubleArray[1] = value.doubleArray[1]
    }

    fun set(real: FloatX, image: FloatX) {
        doubleArray[0] = real
        doubleArray[1] = image
    }

    override fun toString(): String = "$real + ${image}i"

    companion object {
        val ZERO get() = Complex(FloatX_Zero, FloatX_Zero)
    }
}


