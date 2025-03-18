package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.Throws
import kotlin.time.Duration

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
     * @param timeout the maximum time to wait for the latch to open.
     * @return true if the latch was opened, false if timeout occurred.
     */
    @Throws(InterruptedException::class)
    fun await(timeout: Duration): Boolean {

        var timeLeft = timeout.inWholeNanoseconds
        guard.withLock {
            while (true) {

                // Check condition
                if (isOpen)
                    return true

                // Check timeout
                if (timeLeft <= 0)
                    return false

                // Wait for it
                timeLeft = condition.awaitNanos(timeLeft)
            }
        }
    }

    /**
     * Opens the latch, allowing all blocked threads to proceed.
     */
    fun open() {
        guard.withLock {
            if (!isOpen) {
                isOpen = true
                // Notify all waiting threads
                condition.signalAll()
            }
        }
    }
}