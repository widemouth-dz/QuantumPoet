import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

class ComputeTest {
    @Test
    fun float_times_takes() {
        val sd = 1 / sqrt(2.0).toFloat()
        measureNanoTime {
            val result = sd * sd
        }.also { println("float times takes : $it") }
    }
}