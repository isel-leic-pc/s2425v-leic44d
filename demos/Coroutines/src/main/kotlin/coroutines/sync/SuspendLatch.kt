package coroutines.sync

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SuspendLatch {

    private val guard = Mutex()
    private val continuations = mutableListOf<Continuation<Unit>>()
    private var isOpen = false

    suspend fun await() =
        guard.withLock {
            if (!isOpen) {
                suspendCoroutine { continuation ->
                    continuations.add(continuation)
                }
            }
        }

    suspend fun open() {
        val toContinue = guard.withLock {
            isOpen = true
            continuations.toList()
        }

        toContinue.forEach { it.resume(Unit) }
    }
}