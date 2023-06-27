import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BitTest {
    @Test
    fun op_order() {
        assertEquals(3, 2 and 3 or 1)

        assertEquals(2, 2 and 3 - 1)

        assertEquals(2, 4 - 2 and 3 - 1)
    }


    @Test
    fun overflow() {
        assertEquals(0xFF_FF_FF_FF.toInt(), ((1L shl 0xFF_FF_FF_FF.countOneBits()) - 1).toInt())
        assertEquals(0x0F_FF_FF_FF, (1 shl 0x0F_FF_FF_FF.countOneBits()) - 1)
    }

}