package isel.leic.pc.demos

import java.util.concurrent.CountDownLatch

/**
 * Starts [threadCount] threads, each one executing [threadCode], and blocks the calling thread until all launched
 * threads terminate.
 */
fun runBlocking(threadCount: Int, threadCode: () -> Unit) {
    val threads = CountDownLatch(threadCount)
    repeat(threadCount) {
        Thread.ofPlatform().start {
            try { threadCode() }
            finally { threads.countDown() }
        }
    }
    threads.await()
}
