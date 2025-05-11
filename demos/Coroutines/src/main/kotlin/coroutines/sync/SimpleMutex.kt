package coroutines.sync

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SimpleMutex {

    private var isLocked = false
    private val waiters = mutableListOf<Continuation<Unit>>()
    private val guard = ReentrantLock()

    private suspend fun lock() {
        guard.lock()
        if (isLocked) {
            suspendCoroutine { continuation ->
                waiters.add(continuation)
                guard.unlock()
            }
        }
    }

    private fun unlock() {
        guard.lock()
        val nextWaiter = waiters.removeFirstOrNull()
        if (nextWaiter == null) {
            isLocked = false
        } else {
            nextWaiter.resume(Unit)
        }
        guard.unlock()

    }

    suspend fun withLock(action: () -> Unit) {
        lock()
        try {
            action()
        } finally {
            unlock()
        }
    }
}