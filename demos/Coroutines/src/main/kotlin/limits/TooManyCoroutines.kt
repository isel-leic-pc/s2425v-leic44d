package limits

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Limits demo")

fun main() {
    logger.info("Before runBlocking")
    runBlocking {
        repeat(1_000_000) { iteration ->
            launch {
                if (iteration % 1_000 == 0) {
                    logger.info("Coroutine $iteration")
                }
                delay(1000)
            }
        }
    }
}