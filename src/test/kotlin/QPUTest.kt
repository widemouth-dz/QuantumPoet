import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class QPUTest {

    @Test
    fun qpu() {
        QPU(2) {
            write(0)
            hadamard(1)
            conditionNot(2, 1)
            val result = read() // 00B or 11B
            println(result.toString(2))
        }
    }

    @Test
    fun hadamard() {
        val qpu = QPU(20)
        qpu.hadamard()
        qpu.print(4)
    }

    @Test
    fun conditionHadamard() {
        val qpu = QPU(28)
        qpu.conditionHadamard((1 shl 20) - 1, 2)
        qpu.print(4)
    }

    @Test
    fun not() {
        val qpu = QPU(25)
        qpu.not(1)
        qpu.print(4)
    }

    @Test
    fun write() {
        val qpu = QPU(25)
        qpu.write(1)
        qpu.print(4)
    }

    @Test
    fun read() {
        val qpu = QPU(28)
        qpu.hadamard()
        val result = qpu.read()
        println(result.toString(2))
        qpu.print(4)
    }

    @Test
    fun phase() {
        val qpu = QPU(4)
        qpu.hadamard()
        qpu.phaseShift(90.toFloatX(), 1)
        qpu.print(4)
    }

    private fun QPU.print(count: Int = qBitCount) {
        println("states:")

        mQStateTermMap.asSequence().take(1 shl count)
            .forEachIndexed { index, qStateTerm ->
                print("[$index(${qStateTerm.vector.modulusSquare}): ${qStateTerm.vector}] ")
            }

        println()
        println("$totalProbability:" + totalProbability.toBits().toString(2))
        assertTrue(totalProbability > 0.999_99)
    }
}