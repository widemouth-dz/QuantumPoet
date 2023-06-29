@JvmInline
value class QStateTermMap private constructor(private val values: FloatXArray) : Collection<QStateTerm>, RandomAccess {

    constructor(size: Int) : this(values = FloatXArray(size shl 1) { FloatX_Zero }.apply { this[0] = FloatX_One })

    override val size get() = values.size shr 1

    operator fun get(index: Int): QStateTerm {
        val phyIndex = index shl 1
        return QStateTerm(Complex(values[phyIndex], values[phyIndex + 1]))
    }

    operator fun set(index: Int, model: QStateTerm) {
        val phyIndex = index shl 1
        values[phyIndex] = model.vector.real
        values[phyIndex + 1] = model.vector.image
    }

    override fun iterator(): Iterator<QStateTerm> = QStateTermIterator(this)

    override fun isEmpty(): Boolean = values.isEmpty()

    @Deprecated(message = "Not yet implemented", level = DeprecationLevel.HIDDEN)
    override fun containsAll(elements: Collection<QStateTerm>): Boolean = error("Not yet implemented")

    @Deprecated(message = "Not yet implemented", level = DeprecationLevel.HIDDEN)
    override fun contains(element: QStateTerm): Boolean = error("Not yet implemented")
}



