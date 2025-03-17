package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A latch is a synchronization primitive used to make sure that multiple threads do not proceed until a certain
 * condition is met. When the condition is met, the latch is "opened" and all threads are allowed to proceed.
 */
class Latch {

    private var isOpen = false

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    /**
     * Blocks the calling thread until the latch is opened.
     * TODO: Change the method's signature so cancellation and timeouts are supported.
     */
    fun await() {
        guard.withLock {
            if (isOpen)
                return

            // Wait for it
            condition.await()

            TODO()
        }
    }

    /**
     * Opens the latch, allowing all blocked threads to proceed.
     */
    fun open() {
        guard.withLock {
            isOpen = true
            // Notify all waiting threads
            condition.signalAll()
        }
    }
}