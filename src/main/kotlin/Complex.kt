import kotlin.math.atan2
import kotlin.math.sqrt

// typealias Complex = Pair<Double, Double>

data class Complex(val real: Double, val image: Double) {
    val modulusSquare get() = real * real + image * image
    val modulus get() = sqrt(modulusSquare)
    val argument get() = atan2(image, real)
    operator fun Complex.plus(other: Complex) = Complex(real + other.real, image + other.image)
}


