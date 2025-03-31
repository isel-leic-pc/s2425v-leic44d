package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class CyclicLatchGeneration {

    private var generationCounter = 1

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    @Throws(InterruptedException::class)
    fun await(timeout: Duration): Boolean {
        guard.withLock {

            val myGeneration = generationCounter
            var remainingTime = timeout.inWholeNanoseconds

            while (true) {
                // Wait for it
                remainingTime = condition.awaitNanos(remainingTime)

                if (myGeneration < generationCounter)
                    return true

                if (remainingTime <= 0)
                    return false
            }
        }
    }

    fun open() {
        guard.withLock {
            // Notify all waiting threads
            generationCounter += 1
            condition.signalAll()
        }
    }
}
