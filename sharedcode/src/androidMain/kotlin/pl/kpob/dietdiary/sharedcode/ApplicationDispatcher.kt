package pl.kpob.dietdiary.sharedcode

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.AndroidClientEngine
import io.ktor.client.engine.android.AndroidEngineConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default
internal actual val httpClientEngine: HttpClientEngine
    get() = AndroidClientEngine(AndroidEngineConfig())
internal actual val LOCALHOST: String = "10.0.2.2"