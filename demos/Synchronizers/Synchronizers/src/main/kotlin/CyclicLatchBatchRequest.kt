package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.jvm.Throws
import kotlin.time.Duration

class CyclicLatchBatchRequest {

    data class Request(
        var signalled: Boolean = false,
        var waiting: Int = 1
    )

    private var sharedRequest: Request? = null

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private fun removeRequest() {
        sharedRequest?.let {
            it.waiting -= 1
        }

        if (sharedRequest?.waiting == 0) {
            sharedRequest = null
        }
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Duration): Boolean {
        guard.withLock {

            sharedRequest = sharedRequest ?: Request()
            val myRequest = sharedRequest

            var remainingTime = timeout.inWholeNanoseconds

            try {
                while (true) {
                    // Wait for it
                    remainingTime = condition.awaitNanos(remainingTime)

                    if (myRequest?.signalled == true) {
                        return true
                    }

                    if (remainingTime <= 0) {
                        removeRequest()
                        return false
                    }
                }
            } catch (ie: InterruptedException) {
                removeRequest()
                throw ie
            }
        }
    }

    fun open() {
        guard.withLock {
            // Notify all waiting threads
            sharedRequest?.signalled = true
            sharedRequest = null
            condition.signalAll()
        }
    }
}
