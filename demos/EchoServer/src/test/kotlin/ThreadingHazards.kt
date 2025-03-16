package isel.leic.pc.demos

import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

const val N_THREADS = 20
const val REPS = 100_000

/**
 * Tests illustrating threading hazards and possible solutions.
 */
class ThreadingHazards {
    @Test
    fun `lost updates`() {
        var counter = 0
        runBlocking(N_THREADS) {
            repeat(times = REPS) {
                counter += 1
            }
        }

        // We expect counter NOT TO BE equal to REPS * N_THREADS
        assertNotEquals(illegal = REPS * N_THREADS, actual = counter)
    }

    @Test
    fun `no lost updates with atomics`() {
        val counter = AtomicInteger(0)
        runBlocking(N_THREADS) {
            repeat(times = REPS) {
                counter.incrementAndGet()
            }
        }

        // We expect counter to be equal to REPS * N_THREADS
        assertEquals(expected = REPS * N_THREADS, actual = counter.get())
    }

    @Test
    fun  `no lost updates with explicit locks`() {
        val lock = ReentrantLock()
        var counter = 0
        runBlocking(N_THREADS) {
            repeat(times = REPS) {
                lock.withLock {
                    counter += 1
                }
            }
        }
    }

    @Test
    fun `this is a deadlock`() {
        val lock1 = ReentrantLock()
        val lock2 = ReentrantLock()
        val t1 = Thread.ofPlatform().start {
            lock1.withLock {
                Thread.sleep(1000)
                lock2.withLock {
                    println("Thread 1 with both locks")
                }
            }
        }

        val t2 = Thread.ofPlatform().start {
            lock2.withLock {
                Thread.sleep(1000)
                lock1.withLock {
                    println("Thread 2 with both locks")
                }
            }
        }

        assertFalse(t1.join(Duration.ofSeconds(3)))
        assertFalse(t2.join(Duration.ofSeconds(3)))
    }
}
