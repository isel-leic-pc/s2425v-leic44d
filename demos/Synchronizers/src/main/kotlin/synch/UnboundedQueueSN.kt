package isel.leic.pc.demos.synch

import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import kotlin.concurrent.withLock
import kotlin.time.Duration

/**
 * Class whose instances represent <i>unbounded queues</i>.
 *
 * An <i>unbounded queue</i> is a synchronization object used to support communication
 * between threads that produce items to be used (consumed) by other threads. The former are
 * named <i>producers</i>, the latter are named <i>consumers</i>.
 *
 * This type of synchronization object is useful when the expected production rate is lower than
 * the rate of consumption. In such scenario, memory exhaustion provoked by the absence of bounds
 * does not occur.
 *
 * Notice that this implementation has a FIFO policy for servicing contending threads.
 *
 * Implementation note: the implementation optimizes the number of context switches.
 */
class UnboundedQueueSN<T> {

    private val queue = LinkedList<T>()

    private val guard = ReentrantLock()
    private data class Request<T>(var item: T? = null, val condition: Condition)
    private val requests = LinkedList<Request<T>>()

    /**
     * Adds the given element to the queue.
     * @param item The element to be added to the queue
     */
    fun put(item: T) {
        guard.withLock {
            if (requests.isNotEmpty()) {
                val request = requests.removeFirst()
                request.item = item
                request.condition.signal()
            }
            else {
                queue.addLast(item)
            }
        }
    }

    /**
     * Removes an element from the queue. The calling thread is blocked until an element becomes available,
     * the specified time as elapsed, or the thread is cancelled.
     * @return The element removed from the queue or <code>null</code> if the specified time elapses
     * before an element becomes available.
     * @throws InterruptedException If the blocked thread has been signaled for cancellation.
     */
    @Throws(InterruptedException::class)
    fun take(timeout: Duration): T? {
        guard.withLock {

            if (queue.isNotEmpty()) {
                return queue.removeFirst()
            }

            var remainingTime = timeout.inWholeNanoseconds
            val myRequest = Request<T>(condition = guard.newCondition())
            requests.addLast(myRequest)

            try {
                while (true) {
                    remainingTime = myRequest.condition.awaitNanos(remainingTime)

                    if (myRequest.item != null) {
                        return myRequest.item
                    }

                    if (remainingTime <= 0) {
                        requests.remove(myRequest)
                        return null
                    }
                }
            }
            catch (e: InterruptedException) {
                requests.remove(myRequest)
                throw e
            }
        }
    }
}