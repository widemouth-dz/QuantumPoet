@JvmInline
value class Bits(private val values: IntArray) {

    fun getBits(shift: Int, mask: Int): Int {
        val intIndex = shift / Int.SIZE_BITS
        if (intIndex !in values.indices) return 0
        val bitIndex = shift % Int.SIZE_BITS
        if (bitIndex == 0) return values[intIndex] and mask
        val lowBits = values[intIndex] shr bitIndex
        val highBits = values.getOrElse(intIndex + 1) { 0 } shl (Int.SIZE_BITS - bitIndex)
        return lowBits or highBits and mask
    }

    fun setBits(shift: Int, mask: Int, bits: Int) {
        val intIndex = shift / Int.SIZE_BITS
        if (intIndex !in values.indices) return
        val bitIndex = shift % Int.SIZE_BITS

        if (bitIndex == 0) {
            values[intIndex] = values[intIndex] and mask.inv() or bits
            return
        } else {
            if (intIndex + 1 < values.size) {
                val alignMask = mask shr (Int_SIZE_BITS - bitIndex)
                val alignBits = bits shl (Int_SIZE_BITS - bitIndex)
                values[intIndex + 1] = values[intIndex + 1] and alignMask.inv() or alignBits
            }
            val alignMask = mask shl bitIndex
            val alignBits = bits shl bitIndex
            values[intIndex] = values[intIndex] and alignMask.inv() or alignBits
        }
    }

    companion object {
        const val Int_SIZE_BITS = Int.SIZE_BITS
    }
}
