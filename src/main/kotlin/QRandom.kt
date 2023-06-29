import kotlin.random.Random

object QRandom {
    fun nextBit(probability: FloatX) = if (Random.nextFloat() < probability) 1 else 0
}