package coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StructuredConcurrencyDemo")

fun main() {

    logger.info("main start")
    runBlocking(Dispatchers.IO) {
        logger.info("runBlocking start")
        lateinit var innerJob: Job
        val outerJob = launch {
            logger.info("outerJob start")
            innerJob = launch {
                logger.info("innerJob start")
                Thread.sleep(3000)
                //delay(3000)
                delay(10)
                logger.info("innerJob ends")
            }
            delay(1000)
            logger.info("outerJob ends")
        }

        delay(1200)
        outerJob.cancel()
    }
    logger.info("main ends")
}