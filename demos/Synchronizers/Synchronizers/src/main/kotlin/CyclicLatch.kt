package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CyclicLatch {

    var isOpen = false

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    fun await() {
        guard.withLock {
            while (true) {

                // Check condition
                if (isOpen)
                    return

                // Wait for it
                condition.await()
            }
        }
    }

    fun open() {
        guard.withLock {
            if (!isOpen) {
                isOpen = true
                // Notify all waiting threads
                condition.signalAll()

                TODO("This implementation is wrong")
                isOpen = false
            }
        }
    }
}