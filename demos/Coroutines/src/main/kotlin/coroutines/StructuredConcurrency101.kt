package coroutines

import coroutines.sync.SimpleMutex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StructuredConcurrencyDemo")

fun main() {

    val mutex = Mutex()
    var someValue = 0

    logger.info("main start")
    runBlocking {
        launch {
            repeat(100_000) {
                mutex.withLock {
                    someValue += 1
                }
                if (it % 1000 == 0) {
                    delay(10)
                }
            }
        }

        launch {
            repeat(100_000) {
                mutex.withLock {
                    someValue += 1
                }
                if (it % 1000 == 0) {
                    delay(10)
                }
            }
        }
    }

    logger.info("someValue: $someValue")
    logger.info("main ends")
}