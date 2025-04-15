package coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep

private val logger = LoggerFactory.getLogger("Hello coroutines demo")

fun main() {
    logger.info("Before runBlocking")
    runBlocking {
        launch {
            sleep(10_000)
            delay(1500)
            logger.info("Hello from coroutine 1")
            delay(500)
            logger.info("Hello again from coroutine 1")
        }

        launch {
            delay(1000)
            logger.info("Hello from coroutine 2")
            delay(1000)
            logger.info("Hello again from coroutine 2")
        }
    }
    logger.info("After runBlocking")
}