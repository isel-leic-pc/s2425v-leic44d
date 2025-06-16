package coroutines.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun <T,U> both(f1: suspend () -> T, f2: suspend () -> U): Pair<T,U> =
    coroutineScope {
        val job1 = async { f1() }
        val job2 = async { f2() }
        Pair(job1.await(), job2.await())
    }

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> one(f1: suspend () -> T, f2: suspend () -> T): T =
    coroutineScope {
        val job1 = async { f1() }
        val job2 = async { f2() }

        var result: T? = null
        job1.invokeOnCompletion {
            if (!job1.isCancelled) {
                job2.cancel()
                result = job1.getCompleted()
            }
        }

        job2.invokeOnCompletion {
            if (!job2.isCancelled) {
                job1.cancel()
                result = job2.getCompleted()
            }
        }

        result ?: throw IllegalStateException("Both jobs were cancelled or failed")
    }