package pl.kpob.dietdiary.coroutines

import kotlinx.coroutines.*

class ScreenJobManager {

    private lateinit var parentJob: Job
    private val ioScope get() = CoroutineScope(Dispatchers.IO + parentJob)

    fun start() {
        parentJob = Job()
    }

    fun execute(block: suspend CoroutineScope.() -> Unit) {
        ioScope.launch(block = block)
    }

    suspend fun onUIThread(block: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.Main, block)
    }

    fun cancel() {
        parentJob.cancel()
    }
}