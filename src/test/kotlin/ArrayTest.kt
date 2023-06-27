import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class ArrayTest {
    @Test
    fun storage_speed() {

        val list = mutableListOf<Any>()

        val shift = 27

        println("SplitArray takes " + measureTimeMillis {
            DoubleArray(1 shl shift - 1) { 0.0 }.apply { this[0] = 0.0 }
            DoubleArray(1 shl shift - 1) { 0.0 }
        })

        println("TotalArray takes " + measureTimeMillis {
            DoubleArray(1 shl shift - 1) { 0.0 }.apply { this[0] = 0.0 }
        })

        println("PairArray takes " + measureTimeMillis {
            Pair(DoubleArray(1 shl shift - 1) { 0.0 }, DoubleArray(25 shl shift - 1) { 0.0 })
        })

    }

}