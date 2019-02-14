package pl.kpob.dietdiary.sharedcode

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.TypeInfo
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.IosClientEngine
import io.ktor.client.engine.ios.IosClientEngineConfig
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.response.HttpResponse
import io.ktor.http.content.OutgoingContent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext

internal actual val ApplicationDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())

internal class NsQueueDispatcher(
        private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}

internal actual val httpClientEngine: HttpClientEngine
    get() = IosClientEngine(IosClientEngineConfig())
internal actual val LOCALHOST: String
    get() = "localhost"