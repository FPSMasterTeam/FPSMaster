package top.fpsmaster.modules.client

import java.util.concurrent.*

class AsyncTask(threadCount: Int) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(threadCount)

    fun <T> execute(task: Callable<T>): Future<T> = executorService.submit(task)

    fun runnable(task: Runnable): Future<*> = executorService.submit(task)

    fun close() {
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
        } catch (ex: InterruptedException) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
