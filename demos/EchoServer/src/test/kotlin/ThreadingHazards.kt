import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.test.Test
import kotlin.test.assertEquals
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
}
