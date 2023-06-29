import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
val QCDispatcher = Dispatchers.Default.limitedParallelism(4)
val Dispatchers.QC get() = QCDispatcher