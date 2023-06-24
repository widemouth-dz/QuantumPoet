package quantum

import org.junit.jupiter.api.Test

class SequenceTest {
    @Test
    fun windowed() {
        val sequence = generateSequence(1) { it + 1 }
        val windowedSequence = sequence.take(100).windowed(6, 8, true)
        println(windowedSequence.toList())
    }
}