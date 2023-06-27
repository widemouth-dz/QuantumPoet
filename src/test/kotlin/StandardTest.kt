import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class StandardTest {
    @Test
    fun apply() {
        measureTimeMillis {
            val array = Array(1 shl 20) { Complex(0.toFloatX(), 0.toFloatX()) }
            array.forEachIndexed { index, fl ->
                val element = array[index]
                element.set(0.toFloatX(), 0.toFloatX())
                array[index] = element
//                array[index] = array[index].apply { set(0.toFloatX(), 0.toFloatX()) }
            }
        }.also { println("takes: $it ms") }
    }

}