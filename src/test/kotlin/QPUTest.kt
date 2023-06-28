import org.junit.jupiter.api.Test
import quantum.QPU
import kotlin.test.assertTrue

class QPUTest {
    @Test
    fun hadamard() {
        val qpu = QPU(20)
        qpu.hadamard()
        qpu.print(4)
    }

    @Test
    fun conditionHadamard() {
        val qpu = QPU(3)
        qpu.hadamard()
        qpu.conditionHadamard(1 or 2, 4)
//        qpu.conditionHadamard(1, 0)
        qpu.print()
    }

    @Test
    fun not() {
        val qpu = QPU(25)
        qpu.not(1)
//        qpu.print()
    }

    @Test
    fun write() {
        val qpu = QPU(25)
        qpu.write(1)
        qpu.print(4)
    }

    @Test
    fun read() {
        val qpu = QPU(4)
        qpu.hadamard()
        val result = qpu.read()
        println(result.toString(2))
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