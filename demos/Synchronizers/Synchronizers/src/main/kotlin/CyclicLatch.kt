package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CyclicLatch {

    data class Request(var signalled: Boolean = false)

    var isOpen = false
    val requests = mutableListOf<Request>()

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    fun await() {
        guard.withLock {
            // Check condition
            if (isOpen)
                return

            // We will block the current thread
            val myRequest = Request()
            requests.add(myRequest)

            while (true) {
                // Wait for it
                condition.await()

                if (myRequest.signalled)
                    return
            }
        }
    }

    fun open() {
        guard.withLock {
            if (!isOpen) {
                isOpen = true
                // Notify all waiting threads
                while (requests.size > 0)
                    requests.removeFirst().signalled = true
                condition.signalAll()

                isOpen = false
            }
        }
    }
}
