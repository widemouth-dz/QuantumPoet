import org.junit.jupiter.api.Test
import quantum.QPU
import kotlin.test.assertTrue

class QPUTest {
    @Test
    fun hadamard() {
        val qpu = QPU(5)
        qpu.hadamard()
        qpu.print(4)
    }

    @Test
    fun conditionHadamard() {
        val qpu = QPU(2)
        qpu.conditionHadamard(1 or 2, 0)
        qpu.conditionHadamard(1, 0)

        qpu.print()
    }

    @Test
    fun not() {
        val qpu = QPU(28)
        qpu.not(1)
//        qpu.print()
    }

    @Test
    fun write() {
        val qpu = QPU(25)
        qpu.write(12)
    }

    private fun QPU.print(count: Int = qBitCount) {
        println("states:")

        mQStateTermMap.asSequence().take(1 shl count)
            .forEachIndexed { index, qStateTerm ->
                print("[$index(${qStateTerm.vector.modulusSquare}): ${qStateTerm.vector}] ")
            }

        println()
        println("$totalProbability:" + totalProbability.toBits().toString(2))
        assertTrue(totalProbability > 0.999_999)
    }
}