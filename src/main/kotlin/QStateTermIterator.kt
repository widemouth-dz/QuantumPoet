class QStateTermIterator(private val qStateTermMap: QStateTermMap) : Iterator<QStateTerm> {
    private var index = 0

    override fun hasNext(): Boolean = index < qStateTermMap.size

    override fun next(): QStateTerm = qStateTermMap[index++]
}