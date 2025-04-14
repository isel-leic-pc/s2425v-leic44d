package limits

import java.util.concurrent.TimeUnit

fun main() {
    val threads = List(10_000) {
        Thread.ofPlatform().start {
            println("Thread $it is running")
            TimeUnit.SECONDS.sleep(5)
        }
    }
    threads.forEach { it.join() }
}