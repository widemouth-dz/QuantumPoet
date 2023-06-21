import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object QScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}