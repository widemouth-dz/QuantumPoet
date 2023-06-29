import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class ConcurrentTest {
    @Test
    fun concurrent() = runBlocking(Dispatchers.QC) {
        val floatArray = FloatArray(1 shl 29)
        val start = System.currentTimeMillis()
        repeat(100) {
            launch {
                println(Thread.currentThread())
                println("$it start : " + (System.currentTimeMillis() - start))
                floatArray.indices.forEach {
                    floatArray[it] = compute_float()
                }
                println("$it end : " + (System.currentTimeMillis() - start))
            }
        }
    }

    fun compute_float() = 1 / sqrt(2f)
}