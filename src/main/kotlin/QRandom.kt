import kotlin.random.Random

object QRandom {
    fun nextBit(probability: Float) = if (Random.nextFloat() % 1 < probability) 1 else 0
}