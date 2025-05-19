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

    suspend fun await() {
        guard.lock()
        if (!isOpen) {
            suspendCoroutine { continuation ->
                continuations.add(continuation)
                guard.unlock()
            }
            // IMPORTANT: THE MUTEX IS NOT OWNED HERE
        }
        else
            guard.unlock()
    }

    suspend fun open() {
        val toContinue = guard.withLock {
            if (!isOpen) {
                isOpen = true
                continuations.toList().also {
                    continuations.clear()
                }
            }
            else continuations
        }

        toContinue.forEach { it.resume(Unit) }
    }
}