import org.junit.jupiter.api.Test

class ValueClassTest {
    @Test
    fun value_class_generic_box() {
        val mQStateInfos = DoubleArray(1 shl 29) { 0.0 }.apply {
            this[0] = 1.0
        }
    }

    @Test
    fun box_unbox_performance() {


//        (0..(1 shl 30))
//            .asSequence()
//            .map { doubleArrayOf(0.0, 1.0) }
//            .sumOf { it[0] + it[1] }

//        (0..(1 shl 30))
//            .asSequence()
//            .map { Pair(0.0, 1.0) }
//            .sumOf { it.first + it.second }

        (0..(1 shl 30))
            .asSequence()
            .map { OuterValue(InnerValue(doubleArrayOf(0.0, 1.0))) }
            .count()
//            .sumOf { it.innerValue.doubleArray[0] + it.innerValue.doubleArray[1] }
//            .also { println(it) }

//        (0..(1 shl 30))
//            .asSequence()
//            .map { it }
//            .sumOf { 0.0 + 1.0 }


    }

    @JvmInline
    value class OuterValue(val innerValue: InnerValue)

    @JvmInline
    value class InnerValue(val doubleArray: DoubleArray)
}