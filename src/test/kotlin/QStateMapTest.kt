import org.junit.jupiter.api.Test

class QStateMapTest {
    @Test
    fun value_class() {
        val qStateMap = QStateTermMap(1 shl 25)
        val term = qStateMap[3]
    }

    @Test
    fun quantum_state_model() {
        val qStateModel = QStateTerm(Complex(FloatX_Zero, FloatX_One))
    }

    @Test
    fun quantum_state_map() {
        val qStateMap = QStateTermMap(1 shl 25)
        val real = qStateMap[0].vector.real
    }
}