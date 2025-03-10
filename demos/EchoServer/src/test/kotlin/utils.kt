/**
 * Starts [threadCount] threads, each one executing [threadCode], and blocks the calling thread until all launched
 * threads terminate.
 */
fun runBlocking(threadCount: Int, threadCode: () -> Unit) {
    val threads = mutableListOf<Thread>()
    repeat(threadCount) {
        threads.addLast(Thread.ofPlatform().start(threadCode))
    }
    threads.forEach { it.join() }
}
