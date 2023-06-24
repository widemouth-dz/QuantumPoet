package quantum

import org.junit.jupiter.api.Test

class QPUTest {
    @Test
    fun hadamard() {
        val qpu = QPU(2)
        qpu.conditionHadamard(1 or 2, 0)
        qpu.conditionHadamard(1, 0)

        println("states:")
        qpu.mQStates.forEachIndexed { index, state ->
            print("[$index: ${state.complex}] ")
        }

        println()

        println("possibilities:")
        qpu.mQStates.forEachIndexed { index, state ->
            print("[$index: ${state.complex.modulusSquare}] ")
        }

        assert(qpu.mQStates.sumOf { it.complex.modulusSquare } in 0.999_999_999_999_999..1.0)
    }
}